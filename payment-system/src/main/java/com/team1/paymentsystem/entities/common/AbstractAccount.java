package com.team1.paymentsystem.entities.common;

import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.states.AccountStatus;
import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.converters.CurrencyConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@MappedSuperclass
public abstract class AbstractAccount extends StatusObject{
    @NotNull
    @Column(name="account_number", nullable = false)
    private String accountNumber;
    @ManyToOne(targetEntity = Customer.class)
    private Customer owner;
    @Convert(converter = CurrencyConverter.class)
    @Column(nullable = false)
    private Currency currency;
    @Column(name="account_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
}
