package com.team1.paymentsystem.managers;

import com.team1.paymentsystem.dto.filter.ProfileFilterDTO;
import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.common.StatusObject;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.common.OperationManager;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.FilterService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
@Log
public class ProfileManager extends OperationManager<Profile, ProfileDTO> {
    @Autowired
    protected ApplicationContext context;

    @Override
    public OperationResponse findAll(SystemObject systemObject, String username) {
        OperationResponse response = super.findAll(systemObject, username);
        if(response.isValid()){
            List<ProfileDTO> profileDTOList = super.toDTO((List<Profile>) response.getObject());
            response.setDataObject(profileDTOList);
        }
        return response;
    }

    @Override
    public OperationResponse findAllUsable(StatusObject statusObject, String username) {
        OperationResponse response = super.findAllUsable(statusObject, username);
        if(response.isValid()){
            List<ProfileDTO> profileDTOList = super.toDTO((List<Profile>) response.getObject());
            response.setDataObject(profileDTOList);
        }
        return response;
    }

    public OperationResponse filter(ProfileFilterDTO profileFilterDTO){
        FilterService filterService = context.getBean(FilterService.class);
        List<ProfileDTO> filteredProfilesDto = filterService.findFilteredProfiles(profileFilterDTO);
        OperationResponse response = new OperationResponse(filteredProfilesDto);
        return response;
    }
}
