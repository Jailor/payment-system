package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.entities.history.PaymentHistory;
import com.team1.paymentsystem.mappers.history.PaymentHistoryMapper;
import com.team1.paymentsystem.repositories.AccountRepository;
import com.team1.paymentsystem.repositories.history.PaymentHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentHistoryServiceImpl implements PaymentHistoryService{
    @Autowired
    PaymentHistoryMapper paymentHistoryMapper;
    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public PaymentHistory createHistory(Payment obj) {
        PaymentHistory paymentHistory = new PaymentHistory();
        BeanUtils.copyProperties(obj,paymentHistory);
        paymentHistory.setId(0);
        paymentHistory.setOriginalId(obj.getId());
        paymentHistory.setHistoryTimeStamp(LocalDateTime.now());
        return paymentHistory;
    }

    @Override
    public PaymentHistory save(PaymentHistory paymentHistory) {
        return paymentHistoryRepository.save(paymentHistory);
    }

    @Override
    public List<PaymentHistory> findAll() {
        return paymentHistoryRepository.findAll();
    }

    @Override
    public List<PaymentHistory> findByOriginalId(long originalId) {
        return paymentHistoryRepository.findByOriginalId(originalId).orElse(null);
    }

    @Override
    public PaymentHistory findById(long id) {
        return paymentHistoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<PaymentHistory> findByAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        return paymentHistoryRepository.findByCreditAccountOrDebitAccount(account);
    }

    @Override
    public List<PaymentHistory> findByAccountCompleted(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        return paymentHistoryRepository.findByCreditAccountOrDebitAccountCompleted(account);
    }
    @Override
    public List<PaymentHistory> findByAccountFraud(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        return paymentHistoryRepository.findByCreditAccountOrDebitAccountFraud(account);
    }

    @Override
    public List<PaymentHistory> findAllCompleted() {
        return paymentHistoryRepository.findAllCompleted();
    }

    @Override
    public List<PaymentHistory> findAllFraud() {
        return paymentHistoryRepository.findAllFraud();
    }
}
