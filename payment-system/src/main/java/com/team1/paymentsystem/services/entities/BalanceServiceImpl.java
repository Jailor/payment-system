package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Balance;
import com.team1.paymentsystem.repositories.AccountRepository;
import com.team1.paymentsystem.repositories.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceServiceImpl implements BalanceService{
    @Autowired
    protected BalanceRepository balanceRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Override
    public Balance save(Balance balance) {
        if(balance != null){
            return balanceRepository.save(balance);
        }
        return null;
    }

    @Override
    public List<Balance> findAll() {
        return balanceRepository.findAll();
    }

    @Override
    public List<Balance> findByAccount(Account account) {
        return balanceRepository.findByAccount(account).orElse(null);
    }

    @Override
    public Balance findById(long id) {
        return balanceRepository.findById(id).orElse(null);
    }

    @Override
    public Balance findLastBalanceByAccount(Account account) {
        return balanceRepository.findLastBalanceByAccount(account);
    }

    @Override
    public long getAvailableBalance(Balance balance){
        return balance.getAvailableCreditAmount()-balance.getAvailableDebitAmount();
    }
    @Override
    public long getPendingBalance(Balance balance){
        return balance.getPendingCreditAmount()-balance.getPendingDebitAmount();
    }
    @Override
    public long getProjectedBalance(Balance balance){
        return getAvailableBalance(balance) + getPendingBalance(balance);
    }
}
