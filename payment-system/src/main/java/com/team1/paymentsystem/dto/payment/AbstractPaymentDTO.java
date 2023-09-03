package com.team1.paymentsystem.dto.payment;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.PaymentStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPaymentDTO extends SystemDTO {
    private String systemReference;
    private String userReference;
    private String creditAccountNumber;
    private String debitAccountNumber;
    private Long amount;
    private Currency currency;
    private LocalDateTime timeStamp;
    private String stringTimeStamp;
    private PaymentStatus status;
    private Boolean needsApproval;
    // for payment fraud detection
    private Double latitude;
    private Double longitude;
    private Boolean neededApproval;
}
