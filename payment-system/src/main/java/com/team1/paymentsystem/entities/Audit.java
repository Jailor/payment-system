package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.states.Operation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// TODO: the audit is a system object but does not need the versioning. Perhaps
// a different system object should be created for this purpose.
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Audit extends SystemObject {
    @ManyToOne(targetEntity = User.class)
    private User user;
    @Enumerated(EnumType.STRING)
    private Operation operation;
    @Column(name="object_id")
    private Long objectId;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeStamp;
    @Column(name="class_name")
    private String className;
}
