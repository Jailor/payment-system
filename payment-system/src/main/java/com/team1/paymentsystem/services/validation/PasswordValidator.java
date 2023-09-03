package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class PasswordValidator {
    public List<ErrorInfo> validate(String password) {
        List<ErrorInfo> errors = new LinkedList<>();
        if(password.length() < 8){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Password must be at least 8 characters long"));
        }
        if(!password.matches(".*[A-Z].*")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Password must contain at least one uppercase letter"));
        }
        if(!password.matches(".*[a-z].*")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Password must contain at least one lowercase letter"));
        }
        if(!password.matches(".*[0-9].*")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Password must contain at least one digit"));
        }
        if(!password.matches(".*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>\\/?].*")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Password must contain at least one special character"));
        }
        return errors;
    }
}
