package com.team1.paymentsystem.mappers.approve;

import com.team1.paymentsystem.dto.approval.UserApproveDTO;
import com.team1.paymentsystem.entities.common.AbstractUser;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserApproveMapper implements ApproveMapper<AbstractUser, UserApproveDTO> {
    public UserApproveDTO toDTO(AbstractUser oldUser, AbstractUser newUser, Operation operation){
        UserApproveDTO userApproveDTO = new UserApproveDTO();
        BeanUtils.copyProperties(oldUser, userApproveDTO);
        userApproveDTO.setProfileName(oldUser.getProfile().getName());
        userApproveDTO.setNewUsername(newUser.getUsername());
        userApproveDTO.setNewPassword(newUser.getPassword());
        userApproveDTO.setNewEmail(newUser.getEmail());
        userApproveDTO.setNewFullName(newUser.getFullName());
        userApproveDTO.setNewAddress(newUser.getAddress());
        userApproveDTO.setNewStatus(newUser.getNextStatus());
        userApproveDTO.setNewProfileName(newUser.getProfile().getName());
        userApproveDTO.setOperation(operation);
        userApproveDTO.setNeedsApproval(true);
        return userApproveDTO;
    }
}
