package com.team1.paymentsystem.auth;

import com.team1.paymentsystem.dto.register.RegisterDTO;
import com.team1.paymentsystem.managers.response.OperationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

@RestController
@RequestMapping("/api/secure")
public class SecureController {
    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> secureResource(){
        SecureRandom secureRandom = new SecureRandom();
        Integer random = secureRandom.nextInt(1000);
        OperationResponse response = new OperationResponse(random);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
