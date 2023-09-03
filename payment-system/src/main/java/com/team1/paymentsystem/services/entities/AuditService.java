package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.AuditDTO;
import com.team1.paymentsystem.entities.Audit;

import java.util.List;

public interface AuditService{
    Audit save(Audit audit);
    Audit findById(long id);
    Audit toEntity(AuditDTO auditDTO);
    AuditDTO toDTO(Audit entity);
    List<Audit> findAll();
}
