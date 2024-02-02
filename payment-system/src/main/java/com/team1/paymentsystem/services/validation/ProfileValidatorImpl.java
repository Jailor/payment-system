package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.mappers.entity.ProfileMapper;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.repositories.ProfileRepository;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.ProfileRight;
import com.team1.paymentsystem.states.ProfileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static com.team1.paymentsystem.states.ApplicationConstants.maxProfileRights;


@Service
public class ProfileValidatorImpl implements ProfileValidator  {
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    ProfileMapper profileMapper;

    @Override
    public List<ErrorInfo> validate(Profile profile, Operation operation) {
        List<ErrorInfo> errors = new LinkedList<>();
        // validate if null
        if(profile == null){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile is null"));
            return errors;
        }
        // validate profile type
        ProfileType profileType = profile.getProfileType();
        List<ProfileType> profileTypes = List.of(ProfileType.EMPLOYEE, ProfileType.CUSTOMER, ProfileType.ADMINISTRATOR);
        if(profileType == null){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile type is required"));
        }
        else {
            boolean contains = false;
            for(ProfileType profileType1 : profileTypes){
                if(profileType1.equals(profileType)){
                    contains = true;
                    break;
                }
            }
            if(!contains){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile type is invalid"));
            }
        }
        // validate profile name
        String profileName = profile.getName();
        if(profileName == null || profileName.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile name is required"));
        }
        if(profileName != null && profileName.length() <= 3){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile name is too short"));
        }
        if(profileName != null && profileName.length() >= 50){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile name is too long"));
        }

        // validate profile name uniqueness
        Profile fromDB = profileRepository.findByName(profileName).orElse(null);
        if(operation.equals(Operation.CREATE)){
            if(fromDB != null){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile name is already taken"));
            }
        }
        else {
            if(fromDB == null){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile name is not found"));
            }
        }

        // validate profile rights
        String profileRights = profile.getRights();
        if(profileRights == null || profileRights.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile rights are required"));
        }
        else {
            String[] rights = profileRights.split(",");
            List<ProfileRight> profileRightList = List.of(ProfileRight.values());
            boolean contains = false;
            for(String right : rights){
                for(ProfileRight profileRight : profileRightList){
                    if(profileRight.toString().equalsIgnoreCase(right)){
                        contains = true;
                        break;
                    }
                }
            }
            if(!contains){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile rights are invalid"));
            }
        }

        // check for rights to profile type
        String rights = profile.getRights();
        List<ProfileRight> profileRightsList = profileMapper.generateRightsList(rights);
        List<ProfileRight> maxAllowed = maxProfileRights.get(profile.getProfileType());
        if(!new HashSet<>(maxAllowed).containsAll(profileRightsList)){
            if(profileType != null){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Profile right type" + profileType
                        + " does not allow for all the given rights"));
            }
        }

        return errors;
    }
}
