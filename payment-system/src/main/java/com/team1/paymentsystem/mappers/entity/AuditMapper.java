package com.team1.paymentsystem.mappers.entity;

import com.team1.paymentsystem.dto.AuditDTO;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.services.entities.UserService;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuditMapper implements Mapper<AuditDTO, Audit> {
    @Autowired
    UserService userService;
    public AuditDTO toDTO(Audit entity) {
        AuditDTO auditDTO = new AuditDTO();
        BeanUtils.copyProperties(entity, auditDTO);
        auditDTO.setUsername(entity.getUser().getUsername());
        auditDTO.setStringTimeStamp(entity.getTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return auditDTO;
    }

    public Audit toEntity(AuditDTO auditDTO, Operation operation) {
        Audit audit = new Audit();
        BeanUtils.copyProperties(auditDTO, audit);
        User user = userService.findByUsername(auditDTO.getUsername());
        audit.setUser(user);
        if(audit.getTimeStamp() == null) {
            audit.setTimeStamp(LocalDateTime.now());
        }
        return audit;
    }
}
