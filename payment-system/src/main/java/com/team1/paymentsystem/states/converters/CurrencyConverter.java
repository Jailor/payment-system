package com.team1.paymentsystem.states.converters;

import com.team1.paymentsystem.states.Currency;
import jakarta.persistence.AttributeConverter;

public class CurrencyConverter implements AttributeConverter<Currency,String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        if(currency==null){
            return null;
        }
        return currency.getName();
    }

    @Override
    public Currency convertToEntityAttribute(String currency) {
        return Currency.getCurrency(currency);
    }
}
