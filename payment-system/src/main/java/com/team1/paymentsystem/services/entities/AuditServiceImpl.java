package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.AuditDTO;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.mappers.entity.AuditMapper;
import com.team1.paymentsystem.repositories.AuditRepository;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditServiceImpl implements AuditService{
    @Autowired
    protected AuditRepository auditRepository;
    @Autowired
    protected AuditMapper auditMapper;

    @Override
    public Audit save(Audit audit) {
        auditRepository.save(audit);
        return audit;
    }
    @Override
    public Audit findById(long id) {
        return auditRepository.findById(id).orElse(null);
    }
    @Override
    public Audit toEntity(AuditDTO auditDTO, Operation operation) {
        return auditMapper.toEntity(auditDTO, operation);
    }

    @Override
    public AuditDTO toDTO(Audit entity) {
        return auditMapper.toDTO(entity);
    }

    @Override
    public List<Audit> findAll() {
        return auditRepository.findAll();
    }



}
