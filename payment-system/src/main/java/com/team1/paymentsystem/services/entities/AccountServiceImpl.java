package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.mappers.entity.AccountMapper;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.repositories.AccountRepository;
import com.team1.paymentsystem.repositories.AuditRepository;
import com.team1.paymentsystem.services.validation.AccountValidator;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected AccountMapper accountMapper;
    @Autowired
    protected AuditRepository auditRepository;
    @Autowired
    protected AccountValidator accountValidator;
    @Override
    public Account save(Account account) {
        Account fromDb = accountRepository.findByAccountNumber(account.getAccountNumber()).orElse(null);
        if(fromDb != null){
            return null;
        }
        return accountRepository.save(account);
    }

    @Override
    public Account update(Account account) {
        Account fromDB = accountRepository.findById(account.getId()).orElse(null);
        if(fromDB == null){
            return null;
        }
        BeanUtils.copyProperties(account,fromDB);
        return accountRepository.save(fromDB);
    }

    @Override
    public Account remove(long id) {
        Account fromDB = accountRepository.findById(id).orElse(null);
        if(fromDB == null){
            return null;
        }
        accountRepository.delete(fromDB);
        Account account = new Account();
        account.setId(id);
        return account;
    }

    @Override
    public Account remove(Account account) {
        if(account == null){
            return null;
        }
        accountRepository.delete(account);
        return account;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findById(long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account findByDiscriminator(Account obj) {
        return accountRepository.findByAccountNumber(obj.getAccountNumber()).orElse(null);
    }

    @Override
    public Account toEntity(AccountDTO accountDTO, Operation operation) {
        return accountMapper.toEntity(accountDTO, operation);
    }

    @Override
    public List<Account> findNeedsApproval() {
        return accountRepository.findNeedsApproval();
    }

    @Override
    public AccountDTO toDTO(Account entity) {
        return accountMapper.toDTO(entity);
    }

    @Override
    public Account makeCopy(Account obj) {
        Account account = new Account();
        BeanUtils.copyProperties(obj,account);
        return account;
    }

    @Override
    public List<ErrorInfo> validate(Account obj, Operation operation) {
        return accountValidator.validate(obj, operation);
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    @Override
    public List<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public boolean fourEyesCheck(Account obj, String username, int nEyesCheck) {
        int limit = nEyesCheck/2-1;
        List<Audit> audits = auditRepository.findUsersWhichApprovedCheck(obj.getId(),"ACCOUNT");
        if(audits.size() > limit){
            audits = audits.subList(0,limit);
        }
        for(Audit audit : audits){
            if(audit.getUser().getUsername().equals(username)){
                return false;
            }
        }
        return true;
    }
}
