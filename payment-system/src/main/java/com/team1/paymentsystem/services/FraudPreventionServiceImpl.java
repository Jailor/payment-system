package com.team1.paymentsystem.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.paymentsystem.dto.Pair;
import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.PaymentService;
import com.team1.paymentsystem.states.ApplicationConstants;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;

import static com.team1.paymentsystem.states.ApplicationConstants.*;

@Service
@Log
public class FraudPreventionServiceImpl implements FraudPreventionService{
    @Autowired
    PaymentService paymentService;
    @Autowired
    RuleEngine ruleEngine;

    @Autowired
    ApplicationConstants applicationConstants;

    private static final double EARTH_RADIUS = 6371000; // Radius of the Earth in meters

    @Override
    public OperationResponse checkFraud(Payment payment) {
        OperationResponse response = validateFraudCheck(payment);
        if(response.isValid()){
            double[] data = getFraudTensorData(payment);
            log.info("Fraud check data: " + Arrays.toString(data));

            boolean isFraudRuleEngine = ruleEngine.checkFraud(payment);
            boolean isFraudMachineLearning = false;
            // only if the rule engine does not consider it to be fraud, we check with the machine learning model
            if(!isFraudRuleEngine) {
                isFraudMachineLearning = performFraudPrediction(data);
            }

            if(isFraudMachineLearning){
                log.info("Fraud detected by the machine learning model!");
                response.addError(new ErrorInfo(ErrorType.FRAUD_ERROR, "Fraud detected by the machine learning model!"));
            }
            else if(isFraudRuleEngine){
                log.info("Fraud detected by the rule engine!!");
                response.addError(new ErrorInfo(ErrorType.FRAUD_ERROR, "Fraud detected by the rule engine!"));
            }
            else {
                log.info("Fraud not detected!");
            }
        }
        else {
            log.severe("Fraud check cannot be done!");
            for(ErrorInfo errorInfo : response.getErrors()){
                log.severe(errorInfo.toString());
            }
        }
        if(applicationConstants.CHECK_FRAUD){
            return response;
        }
        else {
            return new OperationResponse();
        }
    }

    /**
     * @param payment the payment that is being checked for fraud
     * @return a response with validation errors if the fraud check cannot be done
     */
    private OperationResponse validateFraudCheck(Payment payment) {
        OperationResponse response = new OperationResponse();
        if(!isFraudServiceEnabled){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Fraud service is not available"));
        }
        if(response.isValid()){
            if(payment.getLongitude() == null || payment.getLatitude() == null){
                payment.setLatitude(applicationConstants.HOME_LATITUDE);
                payment.setLongitude(applicationConstants.HOME_LONGITUDE);
                log.info("Payment location is not available. Setting location to home location");
            }
            if(payment.getNeededApproval() == null){
                payment.setNeededApproval(false);
                log.info("Needed approval is not available. Setting needed approval to false");
            }
        }
        return response;
    }

    /**
     * Gets the fraud data for the machine learning model.
     * @param payment the payment that is being checked for fraud
     * @return the data that is used for the fraud check, in the form of a double array
     * @implNote
     * <pre> the data is in the following order: </pre>
     * <pre> 0 - distance from home location </pre>
     * <pre> 1 - distance from last payment </pre>
     * <pre> 2 - ratio to median transaction amount </pre>
     * <pre> 3 - needed approval </pre>
     */
    private double[] getFraudTensorData(Payment payment) {
        double[] data = new double[4];
        // get distance from home location
        Pair<Double,Double> customerLocation = fetchGeolocation(payment.getDebitAccount().getOwner());
        Double customerLatitude = customerLocation.getFirst();
        Double customerLongitude = customerLocation.getSecond();

        Double paymentLatitude = payment.getLatitude();
        Double paymentLongitude = payment.getLongitude();
        data[0] = calculateHaversineDistance(customerLatitude, customerLongitude, paymentLatitude, paymentLongitude) / 1000;
        // get distance from last payment
        Optional<Payment> lastPayment = paymentService.findLastPaymentByDebit(payment.getDebitAccount().getAccountNumber());
        data[1] = 0; // basic case
        if(lastPayment.isPresent()){
            Double lastPaymentLatitude = lastPayment.get().getLatitude();
            Double lastPaymentLongitude = lastPayment.get().getLongitude();
            if(lastPaymentLatitude != null && lastPaymentLongitude != null){
                data[1] = calculateHaversineDistance(lastPaymentLatitude, lastPaymentLongitude, paymentLatitude, paymentLongitude)/ 1000;
            }
        }
        // get ratio of payment amount to median payment amount
        Long returnValue = paymentService.medianPaymentValueDebit(payment.getDebitAccount().getAccountNumber());
        if(returnValue == null) data[2] = 1;
        else data[2] = (double) payment.getAmount() / returnValue;
        // needed approval
        data[3] = payment.getNeededApproval() ? 1 : 0;
        return data;
    }

    /**
     * Performs a fraud prediction by calling the machine learning model
     * with the provided tensor data.
     * @param data the tensor data that will be sent to the machine learning model
     * @return true if the machine learning model detects fraud, false otherwise
     * @implNote the machine learning model is a REST API that returns a JSON response
     */
    private boolean performFraudPrediction(double[] data) {
        String apiUrl = applicationConstants.FRAUD_API_URL;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Custom-Token", applicationConstants.API_TOKEN);

        // Convert double array to JSON string
        String requestBody = "{\"data\": " + Arrays.toString(data) + "}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                boolean isFraud = parsePredictionResponse(responseBody);
                return isFraud;
            } else {
                log.severe("Request failed with status: " + response.getStatusCodeValue());
                return false;
            }
        } catch (Exception e) {
            log.severe("Error sending request: " + e.getMessage());
            return false;
        }
    }

    private boolean parsePredictionResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Get the "prediction" value from the JSON response
            JsonNode predictionNode = jsonNode.get("prediction");
            if (predictionNode != null && predictionNode.isInt()) {
                int prediction = predictionNode.intValue();
                return prediction == 1;
            } else {
                log.severe("Error parsing prediction response: Invalid or missing prediction value");
                return false;
            }
        } catch (Exception e) {
            log.severe("Error parsing prediction response from ML model: " + e.getMessage());
            return false;
        }
    }


    public static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Fetches the geolocation of a customer.
     * @param customer the customer whose location is to be fetched
     * @return a pair of doubles representing the latitude and longitude of the customer's location
     * @implNote the geolocation is found using a geocoding API
     */
    public Pair<Double, Double> fetchGeolocation(Customer customer) {
        String city = customer.getCity();
        String state = customer.getState();
        String country = customer.getCountry();

        if(city == null || state == null || country == null){
            log.severe("Customer location is not available. Setting location to home location");
            return new Pair<>(applicationConstants.HOME_LATITUDE, applicationConstants.HOME_LONGITUDE);
        }
        else {
            String requestUrl = String.format("%s?q=%s,%s,%s&key=%s",
                    applicationConstants.GEOCODING_API_URL, city, state, country, applicationConstants.GEOCODING_API_KEY);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            ResponseEntity<String> response;

            try {
                response = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    String responseBody = response.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);

                    double latitude = jsonResponse.at("/results/0/geometry/lat").asDouble();
                    double longitude = jsonResponse.at("/results/0/geometry/lng").asDouble();
                    log.info("Geolocation: " + latitude + ", " + longitude);
                    return new Pair<>(latitude, longitude);

                } else {
                    log.info("Geolocation request failed with status: " + response.getStatusCode());
                }
            } catch (Exception e) {
                log.info("Error fetching geolocation: " + e.getMessage());
            }
        }
        return new Pair<>(applicationConstants.HOME_LATITUDE, applicationConstants.HOME_LONGITUDE);
    }
}
