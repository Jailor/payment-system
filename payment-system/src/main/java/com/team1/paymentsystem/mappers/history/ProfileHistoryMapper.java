package com.team1.paymentsystem.mappers.history;

import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.mappers.entity.ProfileMapper;
import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.dto.profile.ProfileHistoryDTO;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.history.ProfileHistory;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ProfileHistoryMapper implements Mapper<ProfileHistoryDTO, ProfileHistory> {
    @Autowired
    ProfileMapper profileMapper;
    @Override
    public ProfileHistoryDTO toDTO(ProfileHistory entity) {
        ProfileHistoryDTO profileHistoryDTO = new ProfileHistoryDTO();
        BeanUtils.copyProperties(entity, profileHistoryDTO);
        profileHistoryDTO.setRights(profileMapper.generateRightsList(entity.getRights()));
        profileHistoryDTO.setStringTimeStamp(entity.getTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return profileHistoryDTO;
    }

    // only for create operations
    @Override
    public ProfileHistory toEntity(ProfileHistoryDTO profileHistoryDTO, Operation operation) {
        ProfileDTO profileDTO = new ProfileDTO();
        BeanUtils.copyProperties(profileHistoryDTO, profileDTO);
        Profile profile = profileMapper.toEntity(profileDTO, operation);
        ProfileHistory profileHistory = new ProfileHistory();
        BeanUtils.copyProperties(profileHistoryDTO, profileHistory);
        BeanUtils.copyProperties(profile, profileHistory);
        profileHistory.setId(0);
        profileHistory.setOriginalId(profile.getId());
        if(profileHistory.getTimeStamp() == null){
            profileHistory.setTimeStamp(LocalDateTime.now());
        }
        return profileHistory;
    }
}
