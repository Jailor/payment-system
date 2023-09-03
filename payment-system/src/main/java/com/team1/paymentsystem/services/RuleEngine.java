package com.team1.paymentsystem.services;

import com.team1.paymentsystem.entities.Payment;

public interface RuleEngine {
    boolean checkFraud(Payment payment);
}
