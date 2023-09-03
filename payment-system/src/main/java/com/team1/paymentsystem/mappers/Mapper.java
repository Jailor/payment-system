package com.team1.paymentsystem.mappers;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.entities.common.SystemObject;


public interface Mapper<DTO extends SystemDTO, ENT extends SystemObject> {
    DTO toDTO(ENT entity);
    ENT toEntity(DTO dto);
}
