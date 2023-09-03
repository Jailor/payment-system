package com.team1.paymentsystem.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountHistoryDTO extends AbstractAccountDTO{
    private LocalDateTime timeStamp;
    private String stringTimeStamp;
}
