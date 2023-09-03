package com.team1.paymentsystem.entities.history;

import com.team1.paymentsystem.entities.common.AbstractCustomer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer_history")
public class CustomerHistory extends AbstractCustomer {
    @Column(name = "original_id")
    private long originalId;
    @Column(name = "time_stamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeStamp;
}
