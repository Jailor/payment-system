package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.entities.history.PaymentHistory;

import java.util.List;

public interface PaymentHistoryService extends HistoryService<Payment, PaymentHistory> {
    List<PaymentHistory> findByAccount(String accountNumber);
    List<PaymentHistory> findByAccountCompleted(String accountNumber);
    List<PaymentHistory> findAllCompleted();
    List<PaymentHistory> findByAccountFraud(String accountNumber);
    List<PaymentHistory> findAllFraud();
}
