package com.team1.paymentsystem.mappers.entity;

import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.repositories.ProfileRepository;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.ProfileRight;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ProfileMapper implements Mapper<ProfileDTO, Profile> {
    @Autowired
    protected ProfileRepository profileRepository;
    @Override
    public ProfileDTO toDTO(Profile entity) {
        ProfileDTO profileDTO = new ProfileDTO();
        BeanUtils.copyProperties(entity, profileDTO);
        profileDTO.setRights(generateRightsList(entity.getRights()));
        profileDTO.setNeedsApproval(entity.getNextStateId() != null);
        return profileDTO;
    }

    @Override
    public Profile toEntity(ProfileDTO profileDTO, Operation operation) {
        Profile profile = new Profile();
        BeanUtils.copyProperties(profileDTO, profile);
        Profile db = profileRepository.findByName(profileDTO.getName()).orElse(null);
        if(operation == Operation.CREATE){
            if(db != null) return null;
            if(profileDTO.getRights() == null) return null;
            profile.setRights(generateRightsString(profileDTO.getRights()));
        }
        else // in case of other operations, take the rights from the database
        {
            if(db == null) return null;
            profile.setVersion(db.getVersion());
            profile.setId(db.getId());
            profile.setRights(db.getRights());
        }
        return profile;
    }

    public String generateRightsString(List<ProfileRight> rights) {
        StringBuilder rightsString = new StringBuilder();
        for (ProfileRight right : rights) {
            rightsString.append(right);
            rightsString.append(",");
        }
        if(rightsString.length() >= 1){
            rightsString.deleteCharAt(rightsString.length() - 1);
        }
        return rightsString.toString();
    }

    public List<ProfileRight> generateRightsList(String rights) {
        String op = "";
        for (int i = 0; i < rights.length(); i++) {
            char ch = rights.charAt(i);
            if (!Character.isWhitespace(ch)) {
                op += ch;
            }
        }
        String[] rightsArray = op.split(",");
        List<ProfileRight> rightsList = new LinkedList<>();
        for(String right : rightsArray) {
          rightsList.add(ProfileRight.valueOf(right));
        }
        return rightsList;
    }
}
