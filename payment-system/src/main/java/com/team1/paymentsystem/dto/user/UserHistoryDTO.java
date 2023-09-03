package com.team1.paymentsystem.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryDTO extends AbstractUserDTO{
    private LocalDateTime timeStamp;
    private String stringTimeStamp;
}
