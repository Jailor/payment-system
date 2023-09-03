package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.SystemObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Balance extends SystemObject {
    @ManyToOne(targetEntity = Account.class)
    private Account account;
    @Column(name = "time_stamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeStamp;
    @Column(name = "ACA")
    private Long availableCreditAmount;
    @Column(name = "ADA")
    private Long availableDebitAmount;
    @Column(name = "ACC")
    private Long availableCreditCount;
    @Column(name = "ADC")
    private Long availableDebitCount;
    @Column(name = "PCA")
    private Long pendingCreditAmount;
    @Column(name = "PDA")
    private Long pendingDebitAmount;
    @Column(name = "PCC")
    private Long pendingCreditCount;
    @Column(name = "PDC")
    private Long pendingDebitCount;

    public Balance(Balance src){
        this.account = src.account;
        this.timeStamp = src.timeStamp;
        this.availableCreditAmount = src.availableCreditAmount;
        this.availableDebitAmount = src.availableDebitAmount;
        this.availableCreditCount = src.availableCreditCount;
        this.availableDebitCount = src.availableDebitCount;
        this.pendingCreditAmount = src.pendingCreditAmount;
        this.pendingDebitAmount = src.pendingDebitAmount;
        this.pendingCreditCount = src.pendingCreditCount;
        this.pendingDebitCount = src.pendingDebitCount;
    }
}
