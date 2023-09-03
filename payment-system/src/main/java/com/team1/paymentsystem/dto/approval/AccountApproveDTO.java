package com.team1.paymentsystem.dto.approval;

import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.states.AccountStatus;
import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountApproveDTO extends AccountDTO {
    private Currency newCurrency;
    private Status newStatus;
    private AccountStatus newAccountStatus;
    private Operation operation;
}
