package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.dto.AuditDTO;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.mappers.entity.AuditMapper;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    @Autowired
    AuditService auditService;
    @Autowired
    AuditMapper auditMapper;

    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(){
        List<Audit> audits = auditService.findAll();
        List<AuditDTO> dtos = audits.stream().map(auditMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }
}
