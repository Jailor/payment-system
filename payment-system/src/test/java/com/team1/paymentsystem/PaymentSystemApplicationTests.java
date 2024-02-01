package com.team1.paymentsystem;

import com.team1.paymentsystem.repositories.BalanceRepository;
import com.team1.paymentsystem.repositories.ProfileRepository;
import com.team1.paymentsystem.services.entities.PaymentService;
import com.team1.paymentsystem.states.ApplicationConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;

@SpringBootTest
class PaymentSystemApplicationTests {
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private BalanceRepository balanceRepository;
	@Autowired
	private ApplicationConstants applicationConstants;

	@Test
	void contextLoads() throws InterruptedException {
//		PaymentDTO paymentDTO = new PaymentDTO();
//		// credit, where we ADD money
//		// debit, where we TAKE/SUBTRACT money from
//		paymentDTO.setCreditAccountNumber("ACC92685165732");
//		paymentDTO.setDebitAccountNumber("ACC57293336312");
//		paymentDTO.setCurrency(Currency.USD);
//		paymentDTO.setAmount(10000L);
//		paymentDTO.setTimeStamp(LocalDateTime.now());
//		paymentDTO.setUserReference("example transaction");
//
//
//		PaymentService paymentService = context.getBean(PaymentService.class);
//		OperationManager operationManager = context.getBean(OperationManager.class);
//		OperationResponse response = operationManager.manageOperation(paymentDTO, Operation.CREATE, "admin");
//		Thread.sleep(10);
//		OperationResponse response2 = operationManager.manageOperation(paymentDTO, Operation.CREATE, "admin");
//		Thread.sleep(10);
//
//		PaymentDTO paymentDTO1 = new PaymentDTO();
//		paymentDTO1.setCreditAccountNumber("ACC92685165732");
//		paymentDTO1.setDebitAccountNumber("ACC57293336312");
//		paymentDTO1.setCurrency(Currency.USD);
//		paymentDTO1.setAmount(69L);
//		paymentDTO1.setTimeStamp(LocalDateTime.now());
//		paymentDTO1.setUserReference("example transaction");
//		paymentDTO1.setSystemReference(
//				((PaymentDTO)response2.getObject()).getSystemReference()
//		);
//
//		OperationResponse response3 =
//				operationManager.manageOperation((SystemDTO) response.getObject(), Operation.VERIFY, "admin");
//		Thread.sleep(10);
//		OperationResponse response4 = operationManager
//				.manageOperation((SystemDTO) paymentDTO1,  Operation.REPAIR, "admin");
//		Thread.sleep(10);
//
//
//		/*OperationResponse response5 =
//				operationManagerNew.manageOperation((SystemDTO) response3.getObject(), Operation.APPROVE, "admin");
//		Thread.sleep(10);*/
//		OperationResponse response6 = operationManager
//				.manageOperation((SystemDTO) response4.getObject(),  Operation.VERIFY, "admin");
//		Thread.sleep(10);
//
//		/*OperationResponse response7 =
//				operationManagerNew.manageOperation((SystemDTO) response5.getObject(), Operation.AUTHORIZE, "admin");
//		Thread.sleep(10);
//		OperationResponse response8 = operationManagerNew
//				.manageOperation((SystemDTO) response6.getObject(),  Operation.AUTHORIZE, "admin");
//		Thread.sleep(10);*/
//
//		for(ErrorInfo errorInfo : response.getErrors()){
//			System.out.println(errorInfo.getErrorMessage());
//		}
//		for(ErrorInfo errorInfo : response3.getErrors()){
//			System.out.println(errorInfo.getErrorMessage());
//		}
//
//		List<Payment> payments = paymentService.findByAccountNumber("ACC57293336312");
//		System.out.println(payments.size());
	}

	@Test
	void sendRequest() {
		String apiUrl = applicationConstants.FRAUD_API_URL;
		TestRestTemplate restTemplate = new TestRestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = "{\"data\": [0.5, 1.2, 2.3, 0.8]}";

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				System.out.println("Request sent successfully: " + response.getBody());
			} else {
				System.out.println("Request failed with status: " + response.getStatusCodeValue());
			}
		} catch (Exception e) {
			System.out.println("Error sending request: " + e.getMessage());
		}
	}
}
