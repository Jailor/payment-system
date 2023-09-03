package com.team1.paymentsystem.dto.account;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.AccountStatus;
import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AbstractAccountDTO extends SystemDTO {
    private String accountNumber;
    private String ownerEmail;
    private Currency currency;
    private Status status;
    private AccountStatus accountStatus;
    private Boolean needsApproval;
}
