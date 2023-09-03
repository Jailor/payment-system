package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Balance;

import java.util.List;

public interface BalanceService {
    Balance save(Balance balance);
    List<Balance> findAll();
    List<Balance> findByAccount(Account account);
    Balance findById(long id);
    Balance findLastBalanceByAccount(Account account);

    long getAvailableBalance(Balance balance);
    long getPendingBalance(Balance balance);
    long getProjectedBalance(Balance balance);
}
