package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.SystemObject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeHistory extends SystemObject {
    @ManyToOne(cascade = CascadeType.ALL)
    private Payment payment;
    @ManyToOne(cascade = CascadeType.ALL)
    private ExchangeRate exchangeRate;
    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;
    @ManyToOne(cascade = CascadeType.ALL)
    private Account destinationAccount;
}
