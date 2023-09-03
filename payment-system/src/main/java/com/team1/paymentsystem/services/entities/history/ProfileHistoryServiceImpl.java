package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.history.ProfileHistory;
import com.team1.paymentsystem.repositories.history.ProfileHistoryRepository;
import com.team1.paymentsystem.services.entities.ProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfileHistoryServiceImpl implements ProfileHistoryService{
    @Autowired
    protected ProfileHistoryRepository profileHistoryRepository;
    @Autowired
    protected ProfileService profileService;

    @Override
    public ProfileHistory createHistory(Profile profile) {
        ProfileHistory profileHistory = new ProfileHistory();
       /* Profile db = profileService.findByName(profile.getName());
        if(db == null){
            throw new IllegalArgumentException("Profile with name " + profile.getName() + " does not exist");
        }
        BeanUtils.copyProperties(db, profileHistory);*/

        BeanUtils.copyProperties(profile, profileHistory);
        profileHistory.setId(0);
        profileHistory.setOriginalId(profile.getId());
        profileHistory.setTimeStamp(LocalDateTime.now());
        return profileHistory;
    }

    @Override
    public ProfileHistory save(ProfileHistory profileHistory) {
        profileHistoryRepository.save(profileHistory);
        return findByTimeStampAndOriginalId(profileHistory.getOriginalId(), profileHistory.getTimeStamp());
    }

    @Override
    public List<ProfileHistory> findAll() {
        return profileHistoryRepository.findAll();
    }

    @Override
    public List<ProfileHistory> findByOriginalId(long originalId) {
        return profileHistoryRepository.findByOriginalId(originalId).orElse(null);
    }

    @Override
    public ProfileHistory findById(long id) {
        return profileHistoryRepository.findById(id).orElse(null);
    }


    private ProfileHistory findByTimeStampAndOriginalId(long originalId, LocalDateTime timeStamp) {
        return profileHistoryRepository.findByTimeStampAndOriginalId(timeStamp, originalId).orElse(null);
    }
}
