package com.team1.paymentsystem.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDTO extends PaymentDTO{
    private LocalDateTime historyTimeStamp;
    private String stringHistoryTimeStamp;
}
