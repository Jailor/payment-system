package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.Payment;

public interface PaymentValidator extends Validator<Payment> {
    boolean hasEnoughDebitBalance(Payment payment);
}
