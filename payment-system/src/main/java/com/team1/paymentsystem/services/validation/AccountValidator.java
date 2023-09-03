package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.states.Operation;

import java.util.List;

public interface AccountValidator extends Validator<Account>{
    List<ErrorInfo> validate(Account account, Operation operation);
}
