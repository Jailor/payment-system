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
public class BalanceRevealDTO extends SystemDTO {
    private String accountNumber;
    private LocalDateTime timeStamp;
    private Long availableBalance;
    private Long pendingBalance;
    private Long projectedBalance;
}
