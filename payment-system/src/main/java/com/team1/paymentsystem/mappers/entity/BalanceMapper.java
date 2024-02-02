package com.team1.paymentsystem.mappers.entity;

import com.team1.paymentsystem.dto.balance.BalanceEntryDTO;
import com.team1.paymentsystem.dto.balance.BalanceRevealDTO;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Balance;
import com.team1.paymentsystem.repositories.AccountRepository;
import com.team1.paymentsystem.repositories.BalanceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class BalanceMapper{
    @Autowired
    BalanceRepository balanceRepository;
    @Autowired
    AccountRepository accountRepository;
    public BalanceRevealDTO toDTO(Balance entity) {
        BalanceRevealDTO balanceRevealDTO = new BalanceRevealDTO();
        balanceRevealDTO.setAccountNumber(entity.getAccount().getAccountNumber());
        balanceRevealDTO.setTimeStamp(entity.getTimeStamp());
        balanceRevealDTO.setAvailableBalance(entity.getAvailableCreditAmount()-entity.getAvailableDebitAmount());
        balanceRevealDTO.setPendingBalance(entity.getPendingCreditAmount()-entity.getPendingDebitAmount());
        balanceRevealDTO.setProjectedBalance(balanceRevealDTO.getAvailableBalance() + balanceRevealDTO.getPendingBalance());
        return balanceRevealDTO;
    }

    public Balance toEntity(Account account) {
        Balance balance = new Balance();
        balance.setAvailableCreditAmount(0L);
        balance.setAvailableDebitCount(0L);
        balance.setAvailableDebitAmount(0L);
        balance.setAvailableCreditCount(0L);
        balance.setPendingCreditAmount(0L);
        balance.setPendingDebitCount(0L);
        balance.setPendingDebitAmount(0L);
        balance.setPendingCreditCount(0L);
        balance.setAccount(account);
        balance.setTimeStamp(LocalDateTime.now());
        return balance;
    }
}
