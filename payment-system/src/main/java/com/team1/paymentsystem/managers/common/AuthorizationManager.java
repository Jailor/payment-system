package com.team1.paymentsystem.managers.common;

import com.team1.paymentsystem.entities.*;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.mappers.entity.ProfileMapper;
import com.team1.paymentsystem.services.entities.UserService;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.ProfileRight;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team1.paymentsystem.states.ProfileRight.*;
import static com.team1.paymentsystem.states.ProfileRight.LIST_PROFILE;
@Component
@Transactional(rollbackFor = Exception.class)
@Log
public class AuthorizationManager {
    @Autowired
    protected ApplicationContext context;



    /**
     * Checks whether or not the given user is authorized to perform the given operation on the given object.
     * Requires the operation type.
     * @param systemObject - the object that is being operated on
     * @param username - the username of the user performing the operation
     * @param operation - the operation being performed
     * @return an OperationResponse object containing the errors if the user is not authorized
     */
    public OperationResponse authorizeProfile(SystemObject systemObject, String username, Operation operation) {
        ProfileRight check = getProfileCheck(systemObject, operation);
        OperationResponse operationResponse = new OperationResponse();
        if(check == null){
            operationResponse.addError(new ErrorInfo(ErrorType.AUTHORIZATION_ERROR,
                    "You are not authorized to perform this operation: " + operation
                            + " because the entity does not support such an operation"));
        }

        if (check != null && !authorizeProfileOperation(username, check)) {
            operationResponse.addError(new ErrorInfo(ErrorType.AUTHORIZATION_ERROR,
                    "You are not authorized to perform this operation: " + operation + " because" +
                            "you do not have the right: " + check.name()));
        }
        return operationResponse;
    }

    /**
     * Given a profile right and a username, checks whether the given user's profile has the given profile right.
     * @return true if the user has the given profile right, false otherwise
     */
    private boolean authorizeProfileOperation(String username, ProfileRight profileRight) {
        UserService userService = context.getBean(UserService.class);
        ProfileMapper profileMapper = context.getBean(ProfileMapper.class);
        User dbUser = userService.findByUsername(username);
        Profile profile = dbUser.getProfile();
        if (profile == null) {
            return false;
        }
        List<ProfileRight> profileRights = profileMapper.generateRightsList(profile.getRights());
        return profileRights.contains(profileRight);
    }

    /**
     * Generates the necessary profile right for the given operation based
     * on the given object and the operation that must be performed.
     * @param systemObject - the object that determines the type of right that is required
     * @param operation - the operation being performed
     * @implNote current implementation with if/else is not ideal
     * @return the profile right that is required to perform the given operation on the given object
     */
    public ProfileRight getProfileCheck(SystemObject systemObject, Operation operation) {
        ProfileRight check = null;

        if (systemObject instanceof Payment) {
            check = getProfileRightForPaymentOperation(operation);
        } else if (systemObject instanceof User) {
            check = getProfileRightForUserOperation(operation);
        } else if (systemObject instanceof Account || systemObject instanceof Customer) {
            check = getProfileRightForAccount(operation);
        } else if (systemObject instanceof Profile) {
            check = getProfileRightForProfileOperation(operation);
        }

        return check;
    }

    private ProfileRight getProfileRightForPaymentOperation(Operation operation) {
        ProfileRight right = null;
        if (operation.equals(Operation.CREATE)) {
            right = CREATE_PAYMENT;
        } else if (operation.equals(Operation.VERIFY)) {
            right = VERIFY_PAYMENT;
        } else if (operation.equals(Operation.REPAIR)) {
            right = REPAIR_PAYMENT;
        } else if (operation.equals(Operation.APPROVE)) {
            right = APPROVE_PAYMENT;
        } else if (operation.equals(Operation.CANCEL)) {
            right = CANCEL_PAYMENT;
        } else if (operation.equals(Operation.LIST)) {
            right = LIST_PAYMENT;
        } else if(operation.equals(Operation.AUTHORIZE)){
            right = AUTHORIZE_PAYMENT;
        }else if(operation.equals(Operation.UNBLOCK_FRAUD)){
            right = CREATE_PAYMENT;
        } else if(operation.equals(Operation.CREATE_MOBILE)){
            right = CREATE_PAYMENT;
        }
        return right;
    }

    private ProfileRight getProfileRightForUserOperation(Operation operation) {
        ProfileRight right = null;
        if (operation.equals(Operation.CREATE)) {
            right =  ProfileRight.CREATE_USER;
        } else if (operation.equals(Operation.MODIFY)) {
            right = ProfileRight.MODIFY_USER;
        } else if (operation.equals(Operation.REMOVE)) {
            right = ProfileRight.REMOVE_USER;
        } else if (operation.equals(Operation.APPROVE)) {
            right = ProfileRight.APPROVE_USER;
        } else if (operation.equals(Operation.REJECT)) {
            right = ProfileRight.REJECT_USER;
        } else if (operation.equals(Operation.BLOCK)) {
            right = ProfileRight.BLOCK_USER;
        } else if (operation.equals(Operation.UNBLOCK)) {
            right = ProfileRight.UNBLOCK_USER;
        } else if (operation.equals(Operation.LIST)) {
            right = ProfileRight.LIST_USER;
        }
        return right;
    }

    private ProfileRight getProfileRightForAccount(Operation operation) {
        ProfileRight right = null;
        if (operation.equals(Operation.CREATE)) {
            right = CREATE_ACCOUNT;
        } else if (operation.equals(Operation.MODIFY)) {
            right = MODIFY_ACCOUNT;
        } else if (operation.equals(Operation.REMOVE)) {
            right = REMOVE_ACCOUNT;
        } else if (operation.equals(Operation.APPROVE)) {
            right = APPROVE_ACCOUNT;
        } else if (operation.equals(Operation.REJECT)) {
            right = REJECT_ACCOUNT;
        } else if (operation.equals(Operation.LIST)) {
            right = LIST_ACCOUNT;
        } else{
            List<Operation> accStatusOperations
                    = List.of(Operation.BLOCK, Operation.BLOCK_CREDIT, Operation.UNBLOCK_CREDIT, Operation.UNBLOCK,
                    Operation.BLOCK_DEBIT, Operation.UNBLOCK_DEBIT, Operation.CLOSE);
            if(accStatusOperations.contains(operation)){
                right = MODIFY_ACCOUNT;
            }
        }
        return right;
    }

    private ProfileRight getProfileRightForProfileOperation(Operation operation) {
        ProfileRight right = null;
        if (operation.equals(Operation.CREATE)) {
            right =  CREATE_PROFILE;
        } else if (operation.equals(Operation.MODIFY)) {
            right =  MODIFY_PROFILE;
        } else if (operation.equals(Operation.REMOVE)) {
            right =  REMOVE_PROFILE;
        } else if (operation.equals(Operation.APPROVE)) {
            right =  APPROVE_PROFILE;
        } else if (operation.equals(Operation.REJECT)) {
            right =  REJECT_PROFILE;
        } else if (operation.equals(Operation.LIST)) {
            right =  LIST_PROFILE;
        }
        return right;
    }



}
