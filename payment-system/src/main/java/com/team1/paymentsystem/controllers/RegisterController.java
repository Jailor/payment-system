package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.dto.register.RegisterDTO;
import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.managers.CustomerManager;
import com.team1.paymentsystem.managers.RegisterManager;
import com.team1.paymentsystem.managers.UserManager;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {
    @Autowired
    RegisterManager registerManager;
    @Autowired
    CustomerManager customerManager;

    @PostMapping("/api/register")
    public @ResponseBody ResponseEntity<OperationResponse> register(@RequestBody RegisterDTO registerDTO){

        OperationResponse response = registerManager.save(registerDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
