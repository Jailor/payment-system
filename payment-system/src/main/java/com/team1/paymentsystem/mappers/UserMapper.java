package com.team1.paymentsystem.mappers;

import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.repositories.ProfileRepository;
import com.team1.paymentsystem.repositories.UserRepository;
import com.team1.paymentsystem.services.PasswordAuthentication;
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
    public User toEntity(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        Profile profile = profileRepository.findByName(userDTO.getProfileName()).orElse(null);

        if(user.getPassword() != null){ // must be hashed
            PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
            user.setPassword(passwordAuthentication.hash(user.getPassword().toCharArray()));
        }
        else { // must be taken from db
            User dbUser = userRepository.findByUsername(userDTO.getUsername()).orElse(null);
            if(dbUser == null) return null;
            user.setPassword(dbUser.getPassword());
        }
        if(profile!=null){
            user.setProfile(profile);
            return user;
        }
        else {
            User dbUser = userRepository.findByUsername(userDTO.getUsername()).orElse(null);
            if(dbUser == null) return null;
            user.setProfile(dbUser.getProfile());
            return user;
        }
    }
}
