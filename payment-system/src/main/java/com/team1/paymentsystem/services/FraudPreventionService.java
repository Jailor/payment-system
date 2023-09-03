package com.team1.paymentsystem.services;

import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.managers.response.OperationResponse;

/**
 * The fraud prevention service exposes a single method to check if a payment is fraudulent.
 * The payment is then checked against a set of rules and if it is fraudulent, the response
 * will contain an error message. Warning:
 * The response will only contain the errors if the
 * application constant CHECK_FRAUD is set to true.
 */
public interface FraudPreventionService {
    /**
     * Checks a payment for fraud. The payment is checked using both rule engine and machine learning.
     * @param payment the payment to be checked
     * @return the response containing the errors if the payment is fraudulent
     */
    OperationResponse checkFraud(Payment payment);
}
