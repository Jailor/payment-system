package com.team1.paymentsystem.mappers.approve;

import com.team1.paymentsystem.states.Operation;

public interface ApproveMapper<T, DTO> {
    DTO toDTO(T oldEntity, T newEntity, Operation operation);
}
