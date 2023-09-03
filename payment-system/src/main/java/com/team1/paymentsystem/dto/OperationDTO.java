package com.team1.paymentsystem.dto;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.Operation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationDTO extends SystemDTO {
    Operation operation;
}
