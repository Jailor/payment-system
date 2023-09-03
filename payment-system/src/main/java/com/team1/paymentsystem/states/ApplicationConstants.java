package com.team1.paymentsystem.states;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.paymentsystem.dto.json.MaximalSetData;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.team1.paymentsystem.states.Currency.*;

@Log
@Component
public class ApplicationConstants {
    @Value("${custom.token}")
    private String token;
    @Autowired
    ApplicationContext context;
    public static final HashMap<ProfileType, List<ProfileRight>> maxProfileRights = new HashMap<>();
    public static List<String> statuses = new ArrayList<>();
    public static List<Currency> currencies = new ArrayList<>();
    public static List<String> accountStatuses = new ArrayList<>();
    public static List<String> profileTypes = new ArrayList<>();
    public static List<String> paymentStatuses = new ArrayList<>();
    //public static final Long GENERAL_APPROVE_THRESHOLD = 50000L;
    public static Boolean isFraudServiceEnabled = true;
    public static final String FRAUD_API_URL = "http://localhost:5000/predict";
    public static final Double HOME_LATITUDE = 46.75;
    public static final Double HOME_LONGITUDE = 23.59;
    public static final String GEOCODING_API_URL = "https://api.opencagedata.com/geocode/v1/json";
    public static final String GEOCODING_API_KEY = "1576541044ae4f49a80e259ac42d9f6c";
    public static final Boolean CHECK_FRAUD = true;
    public static final boolean N_EYES = false;

    public void loadConstants() {
        loadMaxProfileRights();
        loadStatuses();
        loadCurrencies();
        loadAccountStatuses();
        loadProfileTypes();
        loadPaymentStatuses();
        sendRequest();
    }

    private void loadMaxProfileRights() {
        try {
            // Load the content of maximalSet.json using ResourceLoader
            ResourceLoader resourceLoader = context;
            Resource resource = resourceLoader.getResource("classpath:maximalSet.json");
            InputStream inputStream = resource.getInputStream();

            // Deserialize the JSON content into MaximalSetData object
            ObjectMapper objectMapper = new ObjectMapper();
            MaximalSetData maximalSetData = objectMapper.readValue(inputStream, MaximalSetData.class);

            // Populate the maxProfileRights map with data from maximalSetData
            maxProfileRights.put(ProfileType.ADMINISTRATOR, maximalSetData.getAdministratorRights());
            maxProfileRights.put(ProfileType.EMPLOYEE, maximalSetData.getEmployeeRights());
            maxProfileRights.put(ProfileType.CUSTOMER, maximalSetData.getCustomerRights());

            // Close the input stream
            inputStream.close();
        } catch (IOException e) {
            log.severe("Error loading maximalSet.json: " + e.getMessage());
            // Handle the exception appropriately (e.g., log, throw, or ignore)
        }
        maxProfileRights.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private void loadStatuses() {
        try {
            // Load the content of statuses.json using ResourceLoader
            ResourceLoader resourceLoader = context;
            Resource resource = resourceLoader.getResource("classpath:statuses.json");
            InputStream inputStream = resource.getInputStream();

            // Deserialize the JSON content into a List of strings
            ObjectMapper objectMapper = new ObjectMapper();
            statuses = objectMapper.readValue(inputStream, List.class);

            // Close the input stream
            inputStream.close();
        } catch (Exception e) {
            log.severe("Error loading statuses.json: " + e.getMessage());
            // Handle the exception appropriately (e.g., log, throw, or ignore)
        }
        statuses.forEach(System.out::println);
    }

    private void loadCurrencies() {
        try {
            // Load the content of currency.json using ResourceLoader
            ResourceLoader resourceLoader = context;
            Resource resource = resourceLoader.getResource("classpath:currency.json");
            InputStream inputStream = resource.getInputStream();

            // Deserialize the JSON content into a List of currencies
            ObjectMapper objectMapper = new ObjectMapper();
            currencies = objectMapper.readValue(inputStream, new TypeReference<List<Currency>>() {});
            // Close the input stream
            inputStream.close();

            Map<String, Currency> currencyMap = new HashMap<>();
            currencies.forEach(currency -> currencyMap.put(currency.getName(), currency));
            USD = currencyMap.get("USD");
            EUR = currencyMap.get("EUR");
            RON = currencyMap.get("RON");
        } catch (Exception e) {
            log.severe("Error loading currencies.json: " + e.getMessage());
            // Handle the exception appropriately (e.g., log, throw, or ignore)
        }
        currencies.forEach(System.out::println);
    }

    private void loadAccountStatuses() {
        try {
            // Load the content of accountStatuses.json using ResourceLoader
            ResourceLoader resourceLoader = context;
            Resource resource = resourceLoader.getResource("classpath:accountStatuses.json");
            InputStream inputStream = resource.getInputStream();

            // Deserialize the JSON content into a List of account statuses
            ObjectMapper objectMapper = new ObjectMapper();
            accountStatuses = objectMapper.readValue(inputStream, List.class);

            // Close the input stream
            inputStream.close();
        } catch (Exception e) {
            log.severe("Error loading accountStatuses.json: " + e.getMessage());
            // Handle the exception appropriately (e.g., log, throw, or ignore)
        }
    }

    private void loadProfileTypes() {
        try {
            // Load the content of profileTypes.json using ResourceLoader
            ResourceLoader resourceLoader = context;
            Resource resource = resourceLoader.getResource("classpath:profileTypes.json");
            InputStream inputStream = resource.getInputStream();

            // Deserialize the JSON content into a List of profile types
            ObjectMapper objectMapper = new ObjectMapper();
            profileTypes = objectMapper.readValue(inputStream, List.class);

            // Close the input stream
            inputStream.close();
        } catch (Exception e) {
            log.severe("Error loading profileTypes.json: " + e.getMessage());
            // Handle the exception appropriately (e.g., log, throw, or ignore)
        }
    }

    private void loadPaymentStatuses() {
        try {
            // Load the content of paymentStatuses.json using ResourceLoader
            ResourceLoader resourceLoader = context;
            Resource resource = resourceLoader.getResource("classpath:paymentStatuses.json");
            InputStream inputStream = resource.getInputStream();

            // Deserialize the JSON content into a List of payment statuses
            ObjectMapper objectMapper = new ObjectMapper();
            paymentStatuses = objectMapper.readValue(inputStream, List.class);

            // Close the input stream
            inputStream.close();
        } catch (Exception e) {
            log.severe("Error loading paymentStatuses.json: " + e.getMessage());
            // Handle the exception appropriately (e.g., log, throw, or ignore)
        }
    }

    private void sendRequest() {
        String apiUrl = FRAUD_API_URL;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Custom-Token", token);

        String requestBody = "{\"data\": [0.5, 1.2, 2.3, 0.8]}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println( "Request sent successfully: " + response.getBody());
                ApplicationConstants.isFraudServiceEnabled = true;
            } else {
                System.out.println( "Request failed with status: " + response.getStatusCodeValue());
                ApplicationConstants.isFraudServiceEnabled = false;
            }
        } catch (Exception e) {
            System.out.println( "Error sending request: " + e.getMessage());
            ApplicationConstants.isFraudServiceEnabled = false;
        }
    }
}

