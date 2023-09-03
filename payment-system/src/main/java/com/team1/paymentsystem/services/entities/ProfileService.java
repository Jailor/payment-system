package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.entities.Profile;

import java.util.List;


public interface ProfileService extends GeneralService<Profile, ProfileDTO> {
    Profile save(Profile profile);
    Profile update(Profile profile);
    Profile remove(long id);
    Profile remove(Profile profile);
    List<Profile> findAll();
    Profile findById(long id);
    Profile findByName(String name);
}
