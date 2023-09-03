package com.team1.paymentsystem.managers.common;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.states.Operation;

import java.util.List;

public interface GeneralManager<T extends SystemObject, S extends SystemDTO> {
    S toDTO(T entity);
    List toDTO(List<T> entities);
    T toEntity(S dto);
    OperationResponse makeCopy(T entity);
    OperationResponse authorize(T statusObject, String username, Operation operation);
    OperationResponse nEyesCheck(SystemObject obj, String username, int eyes);
    OperationResponse manageOperation(S dto, Operation operation, String username);
}
