package com.team1.paymentsystem.entities.history;

import com.team1.paymentsystem.entities.common.AbstractAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "account_history_table")
public class AccountHistory extends AbstractAccount {
    @Column(name = "original_id")
    private long originalId;
    @Column(name = "time_stamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeStamp;

}
