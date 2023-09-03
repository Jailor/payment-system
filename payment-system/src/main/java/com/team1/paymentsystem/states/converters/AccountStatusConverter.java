package com.team1.paymentsystem.states.converters;

import com.team1.paymentsystem.states.AccountStatus;
import jakarta.persistence.AttributeConverter;

public class AccountStatusConverter implements AttributeConverter<AccountStatus, String> {

    @Override
    public String convertToDatabaseColumn(AccountStatus accountStatus) {
        if(accountStatus == null){
            return null;
        }
        return accountStatus.getName();
    }

    @Override
    public AccountStatus convertToEntityAttribute(String accountStatus) {
        return new AccountStatus(accountStatus);
    }
}
