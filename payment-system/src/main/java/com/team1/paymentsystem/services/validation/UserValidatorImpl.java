package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.repositories.UserRepository;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class UserValidatorImpl implements UserValidator{
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileValidator profileValidator;
    @Autowired
    EmailValidator emailValidator;
    @Autowired
    PasswordValidator passwordValidator;

    @Override
    public List<ErrorInfo> validate(User user, Operation operation) {
        List<ErrorInfo> errors = new LinkedList<>();
        if(user == null){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "User is required"));
            return errors;
        }
        // validate Profile
        Profile profile = user.getProfile();
        errors.addAll(profileValidator.validate(profile, Operation.MODIFY));
        // validate username
        String username = user.getUsername();
        if(username == null || username.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Username is required"));
        }
        else {
            if(operation.equals(Operation.CREATE)){
                User user1 = userRepository.findByUsername(username).orElse(null);
                if(user1 != null){
                    errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Username is already taken"));
                }
            }
            else {
                User user1 = userRepository.findByUsername(username).orElse(null);
                if(user1 == null){
                    errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Username is not found"));
               }
            }
        }
        // username must be at least 5 characters long
        if(username != null && username.length() < 5){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Username must be at least 5 characters long"));
        }
        // validate password
        String password = user.getPassword();
        if(password == null || password.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Password is required"));
        }
        // validate full name
        String fullName = user.getFullName();
        if(fullName == null || fullName.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Full name is required"));
        }
        // validate email
        String email = user.getEmail();
        if(email == null || email.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Email is required"));
        }
        else {
            errors.addAll(emailValidator.validate(email));
        }
        // check if email already exists for a user with "ACTIVE" status

        if(operation.equals(Operation.CREATE)){
            List<User> users = userRepository.findByEmail(email).orElse(null);
            if(users != null){
                for (User user1 : users){
                    if (user1 != null && user1.getStatus().equals(Status.ACTIVE)) {
                        errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Email is already taken"));
                        return errors;
                    }
                }
            }

        }
        else {
            List<User> users = userRepository.findByEmail(email).orElse(null);
            if(users != null){
                for (User user1 : users){
                    if (user1 != null && user1.getStatus().equals(Status.ACTIVE) && !(user1.getUsername().equals(user.getUsername()))) {
                        errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Email is already taken"));
                    }
                }
            }
        }
        // validate address
        String address = user.getAddress();
        if(address == null || address.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Address is required"));
        }

        return errors;
    }
}
