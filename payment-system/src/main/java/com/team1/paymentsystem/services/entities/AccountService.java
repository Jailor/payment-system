package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.entities.Account;

import java.util.List;

public interface AccountService extends GeneralService<Account, AccountDTO> {
    Account save(Account account);
    Account update(Account account);
    Account remove(long id);
    Account remove(Account account);
    List<Account> findAll();
    Account findById(long id);
    Account findByAccountNumber(String accountNumber);
    List<Account> findByEmail(String email);
}
