package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.repositories.AccountRepository;
import com.team1.paymentsystem.states.AccountStatus;
import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class AccountValidatorImpl implements AccountValidator{
    @Autowired
    CustomerValidator customerValidator;
    @Autowired
    AccountRepository accountRepository;
    @Override
    public List<ErrorInfo> validate(Account account, Operation operation) {
        List<ErrorInfo> errors = new LinkedList<>();
        if(account == null){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Account is required"));
            return errors;
        }
        // validate account number
        String accountNumber = account.getAccountNumber();
        if(accountNumber== null || accountNumber.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Account number is required"));
        }
        else {
            if (!isValidAccountNumber(accountNumber)) {
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Invalid account number format."));
            } else if (!isValidChecksum(accountNumber)) {
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Invalid account number checksum."));
            }
        }
        // validate account user
        errors.addAll(customerValidator.validate(account.getOwner(), Operation.MODIFY));
       // validate currency
        Currency currency = account.getCurrency();
        if(currency == null){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Currency is required"));
        }
        // validate account status
        AccountStatus status = account.getAccountStatus();
        List<AccountStatus> validStatuses = new LinkedList<>();
        validStatuses.addAll(List.of(AccountStatus.OPEN, AccountStatus.BLOCKED_CREDIT,
                AccountStatus.BLOCKED_DEBIT, AccountStatus.BLOCKED, AccountStatus.CLOSED));
        if(status == null){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Account status is required"));
        }
        else if(!validStatuses.contains(status)){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Invalid account status"));
        }
        return errors;
    }

    private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches("^ACC\\d{11}$");
    }

    private boolean isValidChecksum(String accountNumber) {
        int checksum = 0;
        for (int i = 3; i < accountNumber.length() - 1; i++) {
            char digit = accountNumber.charAt(i);
            if (Character.isDigit(digit)) {
                checksum += Character.getNumericValue(digit);
            } else {
                return false;
            }
        }
        int expectedChecksum = checksum % 10;
        int actualChecksum = Character.getNumericValue(accountNumber.charAt(accountNumber.length() - 1));
        return expectedChecksum == actualChecksum;
    }
}
