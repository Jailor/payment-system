package com.team1.paymentsystem.entities.history;

import com.team1.paymentsystem.entities.common.AbstractPayment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payment_history")
public class PaymentHistory extends AbstractPayment {
    @Column(name = "original_id")
    private long originalId;
    @Column(name = "history_time_stamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime historyTimeStamp;
}
