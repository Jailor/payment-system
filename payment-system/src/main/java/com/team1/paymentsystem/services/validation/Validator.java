package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.states.Operation;

import java.util.List;

public interface Validator<T extends SystemObject> {
    List<ErrorInfo> validate(T obj, Operation operation);
}
