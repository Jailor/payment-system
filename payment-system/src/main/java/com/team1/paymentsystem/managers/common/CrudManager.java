package com.team1.paymentsystem.managers.common;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.entities.common.StatusObject;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.response.OperationResponse;

public interface CrudManager<T extends StatusObject, S extends SystemDTO> extends GeneralManager<T, S>{
    OperationResponse save(T obj, String username);
    OperationResponse update(T obj, String username);
    OperationResponse remove(T obj, String username);
    OperationResponse findAll(SystemObject obj, String username);
    OperationResponse approve(T obj, String username);
    OperationResponse reject(T obj, String username);
    OperationResponse findAllUsable(StatusObject obj, String username);
    OperationResponse findById(StatusObject obj, String username);
    OperationResponse findAllNeedsApproval(StatusObject obj, String username);
    OperationResponse findByDiscriminant(StatusObject obj, String username);
    OperationResponse findNeedsApproval(StatusObject obj, String username);
}
