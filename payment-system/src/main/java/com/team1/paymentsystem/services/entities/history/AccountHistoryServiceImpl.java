package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.history.AccountHistory;
import com.team1.paymentsystem.repositories.history.AccountHistoryRepository;
import com.team1.paymentsystem.services.entities.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountHistoryServiceImpl implements AccountHistoryService{
    @Autowired
    protected AccountHistoryRepository accountHistoryRepository;
    @Autowired
    protected AccountService accountService;
    @Override
    public AccountHistory createHistory(Account account) {
        AccountHistory accountHistory = new AccountHistory();
        BeanUtils.copyProperties(account,accountHistory);
        accountHistory.setId(0);
        accountHistory.setOriginalId(account.getId());
        accountHistory.setTimeStamp(LocalDateTime.now());
        return accountHistory;
    }

    @Override
    public AccountHistory save(AccountHistory accountHistory) {
        return accountHistoryRepository.save(accountHistory);
    }

    @Override
    public List<AccountHistory> findAll() {
        return accountHistoryRepository.findAll();
    }

    @Override
    public List<AccountHistory> findByOriginalId(long originalId) {
        return accountHistoryRepository.findByOriginalId(originalId).orElse(null);
    }

    @Override
    public AccountHistory findById(long id) {
        return accountHistoryRepository.findById(id).orElse(null);
    }
}
