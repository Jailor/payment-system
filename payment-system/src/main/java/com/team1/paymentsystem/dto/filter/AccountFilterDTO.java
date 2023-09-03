package com.team1.paymentsystem.dto.filter;

import com.team1.paymentsystem.states.AccountStatus;
import com.team1.paymentsystem.states.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountFilterDTO extends FilterDTO{
    private List<Currency> currencyFilter;
    private List<AccountStatus>accountStatusFilter;
    private String ownerEmail;
}
