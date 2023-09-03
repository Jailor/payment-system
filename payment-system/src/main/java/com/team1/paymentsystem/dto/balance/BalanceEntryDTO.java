package com.team1.paymentsystem.dto.balance;

import com.team1.paymentsystem.dto.common.SystemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceEntryDTO extends SystemDTO {
    private String accountNumber;
    private LocalDateTime timeStamp;
    private Long availableCreditAmount;
    private Long availableDebitAmount;
    private Long availableCreditCount;
    private Long availableDebitCount;
    private Long pendingCreditAmount;
    private Long pendingDebitAmount;
    private Long pendingCreditCount;
    private Long pendingDebitCount;
}
