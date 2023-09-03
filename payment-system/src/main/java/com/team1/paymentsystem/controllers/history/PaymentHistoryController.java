package com.team1.paymentsystem.controllers.history;

import com.team1.paymentsystem.dto.payment.PaymentHistoryDTO;
import com.team1.paymentsystem.mappers.history.PaymentHistoryMapper;
import com.team1.paymentsystem.entities.history.PaymentHistory;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.history.PaymentHistoryService;
import com.team1.paymentsystem.services.entities.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/payment-history")
public class PaymentHistoryController {
    @Autowired
    PaymentHistoryService paymentHistoryService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentHistoryMapper paymentHistoryMapper;

    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll() {
        List<PaymentHistory> paymentHistories = paymentHistoryService.findAll();
        List<PaymentHistoryDTO> dtos = paymentHistories.stream().map(paymentHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findAllByAccountNumber(@PathVariable String accountNumber) {
        List<PaymentHistory> paymentHistories = paymentHistoryService.findByAccount(accountNumber);

        List<PaymentHistoryDTO> dtos = paymentHistories.stream().map(paymentHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }

    @GetMapping("/completed/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findCompletedByAccountNumber(@PathVariable String accountNumber) {
        List<PaymentHistory> paymentHistories = paymentHistoryService.findByAccountCompleted(accountNumber);

        List<PaymentHistoryDTO> dtos = paymentHistories.stream().map(paymentHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }

    @GetMapping("/completed")
    public @ResponseBody ResponseEntity<OperationResponse> findCompleted() {
        List<PaymentHistory> paymentHistories = paymentHistoryService.findAllCompleted();

        List<PaymentHistoryDTO> dtos = paymentHistories.stream().map(paymentHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }

    @GetMapping("/fraud/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findFraudByAccountNumber(@PathVariable String accountNumber) {
        List<PaymentHistory> paymentHistories = paymentHistoryService.findByAccountFraud(accountNumber);

        List<PaymentHistoryDTO> dtos = paymentHistories.stream().map(paymentHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }

    @GetMapping("/fraud")
    public @ResponseBody ResponseEntity<OperationResponse> findFraud() {
        List<PaymentHistory> paymentHistories = paymentHistoryService.findAllFraud();

        List<PaymentHistoryDTO> dtos = paymentHistories.stream().map(paymentHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }

}
