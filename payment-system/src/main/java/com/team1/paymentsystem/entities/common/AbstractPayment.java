package com.team1.paymentsystem.entities.common;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.PaymentStatus;
import com.team1.paymentsystem.states.PaymentType;
import com.team1.paymentsystem.states.converters.CurrencyConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractPayment extends SystemObject {
    @Column(nullable = false, name = "system_reference")
    private String systemReference;

    @Column(nullable = false, name = "user_reference")
    private String userReference;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @ManyToOne(targetEntity = Account.class)
    private Account creditAccount;

    @ManyToOne(targetEntity = Account.class)
    private Account debitAccount;

    @Column(nullable = false)
    private Long amount;

    @Convert(converter = CurrencyConverter.class)
    @Column(nullable = false)
    private Currency currency;

    @Column(name = "time_stamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeStamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payment_status")
    private PaymentStatus status;

    // for payment fraud detection
    private Double latitude;

    private Double longitude;

    @Column(name = "needed_approval")
    private Boolean neededApproval;

    protected AbstractPayment(){
        super();
        type = PaymentType.INTERNAL;
    }
}
