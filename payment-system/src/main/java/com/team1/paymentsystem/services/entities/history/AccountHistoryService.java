package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.history.AccountHistory;

import java.util.List;

public interface AccountHistoryService extends HistoryService<Account, AccountHistory> {
    AccountHistory createHistory(Account account);
    AccountHistory save(AccountHistory accountHistory);
    List<AccountHistory> findAll();
    AccountHistory findById(long id);
}
