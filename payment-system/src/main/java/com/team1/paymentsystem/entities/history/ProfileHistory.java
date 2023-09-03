package com.team1.paymentsystem.entities.history;

import com.team1.paymentsystem.entities.common.AbstractProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "profile_history_table")
public class ProfileHistory extends AbstractProfile {
    @Column(name = "original_id")
    private long originalId;
    @Column(name = "time_stamp")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeStamp;
}
