package com.team1.paymentsystem.auth;

import com.team1.paymentsystem.auth.JwtService;
import com.team1.paymentsystem.dto.login.LoginDTO;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    @Autowired
    LoginService loginService;
    @Autowired
    JwtService jwtTokenUtil;

    @PostMapping("/api/login")
    public @ResponseBody ResponseEntity<OperationResponse> login(@RequestBody LoginDTO loginDTO) {
        OperationResponse response = loginService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if(response.isValid()){
            LoginDTO loginDTO1 = (LoginDTO) response.getObject();
            loginDTO1.setJsonWebToken(jwtTokenUtil.generateToken(loginDTO.getUsername()));
            response.setDataObject(loginDTO1);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/login")
    public String loginTest() {
        return "test";
    }
}
