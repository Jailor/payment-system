package com.team1.paymentsystem.entities.history;

import com.team1.paymentsystem.entities.common.AbstractUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_history")
public class UserHistory extends AbstractUser {
    @Column(name = "original_id")
    private long originalId;
    @Column(name = "time_stamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeStamp;
}
