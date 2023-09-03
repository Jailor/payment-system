package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.states.Operation;

import java.util.List;

public interface UserValidator extends Validator<User> {
    List<ErrorInfo> validate(User user, Operation operation);
}
