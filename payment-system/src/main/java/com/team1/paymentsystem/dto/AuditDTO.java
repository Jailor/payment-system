package com.team1.paymentsystem.dto;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.Operation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditDTO extends SystemDTO {
    private String username;
    private Operation operation;
    private Long objectId;
    private LocalDateTime timeStamp;
    private String className;
    private String stringTimeStamp;
}
