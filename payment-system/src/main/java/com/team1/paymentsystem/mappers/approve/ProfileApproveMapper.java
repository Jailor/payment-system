package com.team1.paymentsystem.mappers.approve;

import com.team1.paymentsystem.dto.approval.ProfileApproveDTO;
import com.team1.paymentsystem.mappers.entity.ProfileMapper;
import com.team1.paymentsystem.entities.common.AbstractProfile;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileApproveMapper implements ApproveMapper<AbstractProfile, ProfileApproveDTO> {
    @Autowired
    ProfileMapper profileMapper;
    @Override
    public ProfileApproveDTO toDTO(AbstractProfile oldEntity, AbstractProfile newEntity, Operation operation) {
        ProfileApproveDTO profileApproveDTO = new ProfileApproveDTO();
        BeanUtils.copyProperties(oldEntity, profileApproveDTO);
        profileApproveDTO.setRights(profileMapper.generateRightsList(oldEntity.getRights()));

        profileApproveDTO.setNewProfileName(newEntity.getName());
        profileApproveDTO.setNewProfileType(newEntity.getProfileType());
        profileApproveDTO.setNewStatus(newEntity.getNextStatus());
        profileApproveDTO.setNewRights(profileMapper.generateRightsList(newEntity.getRights()));
        profileApproveDTO.setOperation(operation);
        profileApproveDTO.setNeedsApproval(true);
        return profileApproveDTO;
    }
}
