package com.team1.paymentsystem.mappers.entity;

import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.repositories.ProfileRepository;
import com.team1.paymentsystem.repositories.UserRepository;
import com.team1.paymentsystem.services.PasswordAuthentication;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<UserDTO,User> {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Override
    public UserDTO toDTO(User entity) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(entity,userDTO);
        userDTO.setProfileName(entity.getProfile().getName());
        userDTO.setNeedsApproval(entity.getNextStateId() != null);
        // Do not remove this line without asking
        userDTO.setPassword(null);
        return userDTO;
    }

    @Override
    public User toEntity(UserDTO userDTO, Operation operation) {
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        Profile profile = profileRepository.findByName(userDTO.getProfileName()).orElse(null);

        if(operation == Operation.CREATE){
            // must be hashed
            if(user.getPassword() == null) return null;
            PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
            user.setPassword(passwordAuthentication.hash(user.getPassword().toCharArray()));
        }
        else { // must be taken from db
            User dbUser = userRepository.findByUsername(userDTO.getUsername()).orElse(null);
            if(dbUser == null) return null;
            user.setPassword(dbUser.getPassword());
        }

        if(operation == Operation.CREATE && profile == null) return null;
        // if a profile has been selected, use it
        if(profile != null){
            user.setProfile(profile);
        } // else grab the existing one from the database
        else {
            User dbUser = userRepository.findByUsername(userDTO.getUsername()).orElse(null);
            if(dbUser == null) return null;
            user.setProfile(dbUser.getProfile());
            user.setVersion(dbUser.getVersion());
            user.setId(dbUser.getId());
        }
        
        return user;
    }
}
