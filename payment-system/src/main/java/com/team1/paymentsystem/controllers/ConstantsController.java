package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.managers.response.OperationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.team1.paymentsystem.states.ApplicationConstants.*;


@RestController
@RequestMapping("/api/constants")
public class ConstantsController {

    @GetMapping("/statuses")
    public @ResponseBody ResponseEntity<OperationResponse> findStatuses(){
        return new ResponseEntity<>(new OperationResponse(statuses), HttpStatus.OK);
    }

    @GetMapping("/account-statuses")
    public @ResponseBody ResponseEntity<OperationResponse> findAccountStatuses(){
        return new ResponseEntity<>(new OperationResponse(accountStatuses), HttpStatus.OK);
    }

    @GetMapping("/currencies")
    public @ResponseBody ResponseEntity<OperationResponse> findCurrencies(){
        return new ResponseEntity<>(new OperationResponse(currencies), HttpStatus.OK);
    }
    @GetMapping("/profile-types")
    public @ResponseBody ResponseEntity<OperationResponse> findProfileTypes(){
        return new ResponseEntity<>(new OperationResponse(profileTypes), HttpStatus.OK);
    }
    @GetMapping("/profile-rights")
    public @ResponseBody ResponseEntity<OperationResponse> findProfileRights(){
        return new ResponseEntity<>(new OperationResponse(maxProfileRights), HttpStatus.OK);
    }

    @GetMapping("/payment-statuses")
    public @ResponseBody ResponseEntity<OperationResponse> findPaymentStatuses(){
        return new ResponseEntity<>(new OperationResponse(paymentStatuses), HttpStatus.OK);
    }
}
