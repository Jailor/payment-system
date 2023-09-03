package com.team1.paymentsystem.services;

import com.team1.paymentsystem.managers.response.OperationResponse;

public interface LoginService {
    OperationResponse login(String username, String password);
}
