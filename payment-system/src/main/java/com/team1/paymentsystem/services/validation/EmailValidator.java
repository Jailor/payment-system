package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailValidator {
    private static final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern pattern = Pattern.compile(regexPattern);

    public List<ErrorInfo> validate(String email) {
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return new LinkedList<>();
        }
        else {
            return List.of( new ErrorInfo(ErrorType.VALIDATION_ERROR,"Email is not valid"));
        }
    }

}
