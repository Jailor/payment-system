package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.mappers.entity.ProfileMapper;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.repositories.AuditRepository;
import com.team1.paymentsystem.repositories.ProfileRepository;
import com.team1.paymentsystem.services.validation.ProfileValidator;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.ProfileRight;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.team1.paymentsystem.states.ApplicationConstants.maxProfileRights;


@Service
public class ProfileServiceImpl implements ProfileService{
    @Autowired
    protected ProfileRepository profileRepository;
    @Autowired
    protected ProfileMapper profileMapper;
    @Autowired
    protected AuditRepository auditRepository;
    @Autowired
    protected ProfileValidator profileValidator;

    @Override
    public Profile save(Profile profile) {
        Profile fromDB = profileRepository.findByName(profile.getName()).orElse(null);
        if (fromDB != null) {
            return null;
        }
        String rights = profile.getRights();
        List<ProfileRight> profileRightsList = profileMapper.generateRightsList(rights);
        List<ProfileRight> maxAllowed = maxProfileRights.get(profile.getProfileType());
        if(new HashSet<>(maxAllowed).containsAll(profileRightsList)){
            return profileRepository.save(profile);
        }
        return null;
    }

    @Override
    public Profile update(Profile profile) {
        Profile fromDB = profileRepository.findById(profile.getId()).orElse(null);
        if (fromDB == null) {
            return null;
        }
        BeanUtils.copyProperties(profile, fromDB);
        return profileRepository.save(fromDB);
    }

    @Override
    public Profile remove(long id) {
        Profile fromDB = profileRepository.findById(id).orElse(null);
        if (fromDB == null) {
            return null;
        }
        profileRepository.delete(fromDB);
        Profile profile = new Profile();
        profile.setId(id);
        return profile;
    }

    @Override
    public Profile remove(Profile profile) {
        if (profile == null) {
            return null;
        }
        profileRepository.delete(profile);
        return profile;
    }

    @Override
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    @Override
    public Profile findById(long id) {
        return profileRepository.findById(id).orElse(null);
    }

    @Override
    public Profile findByDiscriminator(Profile profile) {
        return profileRepository.findByName(profile.getName()).orElse(null);
    }


    @Override
    public Profile findByName(String name) {
        return profileRepository.findByName(name).orElse(null);
    }

    @Override
    public Profile toEntity(ProfileDTO profileDTO, Operation operation) {
        return profileMapper.toEntity(profileDTO, operation);
    }

    @Override
    public List<Profile> findNeedsApproval() {
        return profileRepository.findNeedsApproval();
    }

    @Override
    public ProfileDTO toDTO(Profile entity) {
        return profileMapper.toDTO(entity);
    }

    @Override
    public Profile makeCopy(Profile obj) {
        Profile profile = new Profile();
        BeanUtils.copyProperties(obj, profile);
        return profile;
    }

    @Override
    public List<ErrorInfo> validate(Profile obj, Operation operation) {
        return profileValidator.validate(obj, operation);
    }

    @Override
    public boolean fourEyesCheck(Profile obj, String username,int nEyesCheck) {
        int limit = nEyesCheck/2-1;
        List<Audit> audits = auditRepository.findUsersWhichApprovedCheck(obj.getId(),"PROFILE");
        if(audits.size() > limit){
            audits = audits.subList(0,limit);
        }
        for(Audit audit : audits){
            if(audit.getUser().getUsername().equals(username)){
                return false;
            }
        }
        return true;
    }
}
