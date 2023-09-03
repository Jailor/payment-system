package com.team1.paymentsystem.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerHistoryDTO extends AbstractCustomerDTO{
    private LocalDateTime timeStamp;
    private String stringTimeStamp;
}
