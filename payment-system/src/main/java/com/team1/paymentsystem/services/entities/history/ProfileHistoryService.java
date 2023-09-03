package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.history.ProfileHistory;

import java.util.List;

public interface ProfileHistoryService extends HistoryService<Profile, ProfileHistory> {
    ProfileHistory createHistory(Profile profile);
    ProfileHistory save(ProfileHistory profileHistory);
    List<ProfileHistory> findAll();
    ProfileHistory findById(long id);

}
