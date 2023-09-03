package com.team1.paymentsystem.entities.common;


import com.team1.paymentsystem.states.Status;
import com.team1.paymentsystem.states.converters.StatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
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
    @Convert(converter = StatusConverter.class)
    @Column(nullable = false)
    private Status status;
    @Column(name = "next_status")
    @Convert(converter = StatusConverter.class)
    private Status nextStatus;
    @Column(name = "next_state_id")
    private Long nextStateId;

}
