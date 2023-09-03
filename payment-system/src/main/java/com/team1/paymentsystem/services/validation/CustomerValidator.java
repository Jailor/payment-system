package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.states.Operation;

import java.util.List;

public interface CustomerValidator extends Validator<Customer>  {
    List<ErrorInfo> validate(Customer customer, Operation operation);
}
