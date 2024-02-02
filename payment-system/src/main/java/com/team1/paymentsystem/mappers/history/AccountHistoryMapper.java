package com.team1.paymentsystem.mappers.history;

import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.dto.account.AccountHistoryDTO;
import com.team1.paymentsystem.mappers.entity.AccountMapper;
import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.history.AccountHistory;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AccountHistoryMapper implements Mapper<AccountHistoryDTO, AccountHistory> {
    @Autowired
    AccountMapper accountMapper;
    @Override
    public AccountHistoryDTO toDTO(AccountHistory entity) {
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        BeanUtils.copyProperties(entity,accountHistoryDTO);
        accountHistoryDTO.setOwnerEmail(entity.getOwner().getEmail());
        accountHistoryDTO.setStringTimeStamp(entity.getTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return accountHistoryDTO;
    }

    @Override
    public AccountHistory toEntity(AccountHistoryDTO dto, Operation  operation) {
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(dto,accountDTO);
        Account account = accountMapper.toEntity(accountDTO, operation);
        AccountHistory accountHistory = new AccountHistory();
        BeanUtils.copyProperties(dto,accountHistory);
        BeanUtils.copyProperties(account,accountHistory);
        accountHistory.setId(0);
        accountHistory.setOriginalId(account.getId());
        if(accountHistory.getTimeStamp() == null){
            accountHistory.setTimeStamp(LocalDateTime.now());
        }
        return accountHistory;
    }
}
