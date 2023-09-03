package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.dto.filter.PaymentFilterDTO;
import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.managers.PaymentManager;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.states.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.team1.paymentsystem.controllers.CommonUtils.getUsername;

@RestController
@RequestMapping("api/payment")
public class PaymentController {
    @Autowired
    PaymentManager paymentManager;

    @PostMapping
    public @ResponseBody ResponseEntity<OperationResponse> save(@RequestBody PaymentDTO paymentDTO, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.managePaymentOperation(paymentDTO, Operation.CREATE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/mobile")
    public @ResponseBody ResponseEntity<OperationResponse> saveMobilePayment
            (@RequestBody PaymentDTO paymentDTO, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.managePaymentOperation(paymentDTO, Operation.CREATE_MOBILE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify/{systemReference}")
    public @ResponseBody ResponseEntity<OperationResponse> verify
            (@PathVariable String systemReference, HttpServletRequest request) {
        String username = getUsername(request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setSystemReference(systemReference);
        OperationResponse response = paymentManager.managePaymentOperation(paymentDTO, Operation.VERIFY, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/approve/{systemReference}")
    public @ResponseBody ResponseEntity<OperationResponse> approve
            (@PathVariable String systemReference, HttpServletRequest request) {
        String username = getUsername(request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setSystemReference(systemReference);
        OperationResponse response = paymentManager.managePaymentOperation(paymentDTO, Operation.APPROVE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/authorize/{systemReference}")
    public @ResponseBody ResponseEntity<OperationResponse> authorize
            (@PathVariable String systemReference, HttpServletRequest request) {
        String username = getUsername(request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setSystemReference(systemReference);
        OperationResponse response = paymentManager.managePaymentOperation(paymentDTO, Operation.AUTHORIZE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/unblock-fraud/{systemReference}")
    public @ResponseBody ResponseEntity<OperationResponse> unblockFraud
            (@PathVariable String systemReference, HttpServletRequest request) {
        String username = getUsername(request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setSystemReference(systemReference);
        OperationResponse response = paymentManager.managePaymentOperation(paymentDTO, Operation.UNBLOCK_FRAUD, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cancel/{systemReference}")
    public @ResponseBody ResponseEntity<OperationResponse> cancel
            (@PathVariable String systemReference, HttpServletRequest request) {
        String username = getUsername(request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setSystemReference(systemReference);
        OperationResponse response = paymentManager.managePaymentOperation(paymentDTO, Operation.CANCEL, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/repair")
    public @ResponseBody ResponseEntity<OperationResponse> repair
            (@RequestBody PaymentDTO paymentDTO, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.managePaymentOperation(paymentDTO, Operation.REPAIR, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findAll(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user-payments")
    public @ResponseBody ResponseEntity<OperationResponse> findUserPayments(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findUserPayments(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/completed")
    public @ResponseBody ResponseEntity<OperationResponse> findAllCompleted(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findAllCompleted(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{systemReference}")
    public @ResponseBody ResponseEntity<OperationResponse> findBySystemReference(@PathVariable String systemReference,
                                                                                 HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findBySystemReference(systemReference, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval")
    public @ResponseBody ResponseEntity<OperationResponse> findAllApprovalPayments(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findAllApprovalPayments(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fraud-payments")
    public @ResponseBody ResponseEntity<OperationResponse> findAllFraudPayments(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findAllFraudPayments(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval/account/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findAllApprovalPaymentsByAccount(HttpServletRequest request,
                                                                                            @PathVariable String accountNumber) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findAllApprovalPaymentsByAccount(accountNumber, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fraud-payments/account/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findAllFraudPaymentsByAccount(HttpServletRequest request,
                                                                                            @PathVariable String accountNumber) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findAllFraudPaymentsByAccount(accountNumber, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/account/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findByAccountNumber(@PathVariable String accountNumber,
                                                                               HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = paymentManager.findByAccountNumber(accountNumber, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public @ResponseBody ResponseEntity<OperationResponse> findFilteredPayments(@RequestBody PaymentFilterDTO paymentFilterDTO,
                                                                                HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = paymentManager.findFilteredPayments(paymentFilterDTO, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}