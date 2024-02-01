package com.team1.paymentsystem.entities.common;


import com.team1.paymentsystem.states.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class StatusObject extends SystemObject{
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "next_status")
    @Enumerated(EnumType.STRING)
    private Status nextStatus;

    @Column(name = "next_state_id")
    private Long nextStateId;

}
