package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.entities.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService extends GeneralService<Payment, PaymentDTO>{
    Payment findBySystemReference(String systemReference);
    List<Payment> findByAccountNumber(String accountNumber);
    List<Payment> findByDebitAccountNumberOrderedTimestampCompleted(String debitAccountNumber);
    int getEyes(Payment obj);
    List<Payment> findAllCompleted();
    List<Payment> findUserPayment(String username);
    Optional<Payment> findLastPaymentByDebit(String debitAccountNumber);
    Long medianPaymentValueCredit(String creditAccountNumber);
    Long medianPaymentValueDebit(String debitAccountNumber);
    List<Payment> findNeedsApprovalByAccount(String accountNumber);
    List<Payment> findFraudByAccount(String accountNumber);
    List<Payment> findFraud();
}
