package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.states.Operation;

import java.util.List;

public interface ProfileValidator extends Validator<Profile> {
    List<ErrorInfo> validate(Profile profile, Operation operation);
}
