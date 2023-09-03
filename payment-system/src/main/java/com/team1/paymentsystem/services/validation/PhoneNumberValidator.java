package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhoneNumberValidator {
    private static final String regexPattern = "^\\d{10}$";
    private static final Pattern pattern = Pattern.compile(regexPattern);
    public List<ErrorInfo> validate(String phoneNumber){
        Matcher matcher = pattern.matcher(phoneNumber);
        if(matcher.matches()){
            return new LinkedList<>();
        }
        else{
            return List.of(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Phone number is not valid"));
        }
    }
}
