package com.team1.paymentsystem.mappers.approve;

import com.team1.paymentsystem.dto.approval.CustomerApproveDTO;
import com.team1.paymentsystem.entities.common.AbstractCustomer;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CustomerApproveMapper implements ApproveMapper<AbstractCustomer, CustomerApproveDTO> {

    @Override
    public CustomerApproveDTO toDTO(AbstractCustomer oldEntity, AbstractCustomer newEntity, Operation operation) {
        CustomerApproveDTO customerApproveDTO = new CustomerApproveDTO();
        BeanUtils.copyProperties(oldEntity,customerApproveDTO);
        customerApproveDTO.setDefaultAccountNumber(oldEntity.getDefaultAccountNumber());
        customerApproveDTO.setNewAddress(newEntity.getAddress());
        customerApproveDTO.setNewEmail(newEntity.getEmail());
        customerApproveDTO.setNewName(newEntity.getName());
        customerApproveDTO.setNewPhoneNumber(newEntity.getPhoneNumber());
        customerApproveDTO.setNewStatus(newEntity.getNextStatus());
        customerApproveDTO.setNewCity(newEntity.getCity());
        customerApproveDTO.setNewState(newEntity.getState());
        customerApproveDTO.setNewCountry(newEntity.getCountry());
        customerApproveDTO.setNewDefaultAccountNumber(newEntity.getDefaultAccountNumber());
        customerApproveDTO.setOperation(operation);
        return customerApproveDTO;
    }
}
