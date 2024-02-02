package com.team1.paymentsystem.managers;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.dto.filter.UserFilterDTO;
import com.team1.paymentsystem.dto.login.ChangePasswordDTO;
import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.entities.common.StatusObject;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.common.ManagerUtils;
import com.team1.paymentsystem.managers.common.OperationManager;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.mappers.entity.UserMapper;
import com.team1.paymentsystem.services.FilterService;
import com.team1.paymentsystem.services.LoginService;
import com.team1.paymentsystem.services.PasswordAuthentication;
import com.team1.paymentsystem.services.entities.CustomerService;
import com.team1.paymentsystem.services.entities.UserService;
import com.team1.paymentsystem.services.validation.PasswordValidator;
import com.team1.paymentsystem.states.ApplicationConstants;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.Status;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.java.Log;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
@Log
public class UserManager extends OperationManager<User, UserDTO> {
    @Autowired
    protected ApplicationContext context;
    @Autowired
    protected ApplicationConstants applicationConstants;

    @Override
    public OperationResponse manageOperation(UserDTO userDTO,
                                             Operation operation, String username){
        List<Operation> userStatusOperations = List.of(Operation.BLOCK, Operation.UNBLOCK);
        if(userStatusOperations.contains(operation)){
            return manageUserOperation(userDTO, operation, username);
        } else {
            // before it is hashed
            if(applicationConstants.CHECK_PASSWORD){
                PasswordValidator passwordValidator = context.getBean(PasswordValidator.class);
                List<ErrorInfo> errors = passwordValidator.validate(userDTO.getPassword());
                if(!errors.isEmpty()){
                    OperationResponse response = new OperationResponse();
                    response.setErrors(errors);
                    return response;
                }
            }
            return super.manageOperation(userDTO, operation, username);
        }
    }

    /**
     * Manages the operation of a user,handling the conversion to entity and back to DTO,
     * authorizing with username and validating the payment data in the process.
     * @param userDTO the user to be operated on
     * @param username the username of the user performing the operation, used for authentication
     * @param operation the operation to be performed
     * @return an OperationResponse object containing the result of the operation and the list of associated errors
     */
    public OperationResponse manageUserOperation(UserDTO userDTO,
                                                 Operation operation, String username){
        UserMapper mapper = context.getBean(UserMapper.class);
        User user = mapper.toEntity(userDTO, operation);
        if(user == null){
            OperationResponse response = new OperationResponse();
            response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, "The data" +
                    "sent could not be mapped to an entity. Please check the data sent."));
            return response;
        }

        OperationResponse response = null;
        try {
            response = userStatusUpdate(user, username, operation);
        }
        catch (StaleStateException | OptimisticLockException e){
            response = new OperationResponse();
            response.addError(new ErrorInfo(ErrorType.VERSIONING_ERROR,
                    "The object has been modified by another user. Please refresh the page and try again."));
            return response;
        }

        if(response != null && response.isValid()){
            SystemDTO backDTO = mapper.toDTO((User) response.getObject());
            if(backDTO==null){
                response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, "The data" +
                        "sent could not be mapped to a DTO. Please check the data sent."));
            }
            response.setDataObject(backDTO);
        }

        return response;

    }

    /**
     * Manages the operation of a user status, authorizing the user and validating the data in the process.
     * @param username the username of the user performing the operation, used for authentication
     * @param operation the operation to be performed
     * @return an OperationResponse object containing the result of the operation and the list of associated errors
     */
    private OperationResponse userStatusUpdate(User user, String username, Operation operation) {
        OperationResponse operationResponse = authorize(user, username, operation);
        UserService userService = context.getBean(UserService.class);
        User returnObject = userService.findByDiscriminator(user);

        if(returnObject == null) {
            operationResponse.addError(new ErrorInfo(ErrorType.NOT_FOUND_ERROR,
                    "Object has not been found  (by discriminator)"));
        }
        if(returnObject != null && returnObject.getNextStateId() != null){
            operationResponse.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Entity is already undergoing another modification"));
        }
        if(operationResponse.isValid()) {
            User copyObject = userService.makeCopy(returnObject);
            processUserStatusUpdate(operation, returnObject, copyObject);
            // copy properties to insert into history
            ManagerUtils managerUtils = context.getBean(ManagerUtils.class);
            StatusObject historyObj = (StatusObject) managerUtils.insertHistoryObj(returnObject);

            // add nextStateId
            copyObject.setNextStateId(historyObj.getId());
            copyObject.setNextStatus(returnObject.getNextStatus());

            // update and audit
            copyObject = userService.update(copyObject);
            super.createAudit(copyObject, username, operation);

            operationResponse.setDataObject(copyObject);
        }

        return operationResponse;
    }

    private void processUserStatusUpdate(Operation operation, User returnObject, User copyObject) {
        if (operation.equals(Operation.BLOCK) && copyObject.getStatus().equals(Status.ACTIVE)) {
            returnObject.setStatus(Status.APPROVE);
            returnObject.setNextStatus(Status.BLOCKED);
        } else if (operation.equals(Operation.UNBLOCK) && copyObject.getStatus().equals(Status.BLOCKED)) {
            returnObject.setStatus(Status.APPROVE);
            returnObject.setNextStatus(Status.ACTIVE);
        } else {
            throw new UnsupportedOperationException("Unsupported operation");
        }
    }


    @Override
    public OperationResponse approve(User statusObject, String username) {
        OperationResponse response = super.approve(statusObject, username);
        if(response.isValid()){
            User user = (User) response.getObject();
            String email = user.getEmail();
            CustomerService customerService =  context.getBean(CustomerService.class);
            Customer customer = customerService.findByEmail(email);
            if(customer != null && customer.getStatus().equals(Status.APPROVE)){
                CustomerManager customerManager = context.getBean(CustomerManager.class);
                response.addErrors(customerManager.approve(customer,username).getErrors());
            }
        }
        return response;
    }
    @Override
    public OperationResponse reject(User statusObject, String username) {
        OperationResponse response = super.reject(statusObject, username);
        if(response.isValid()){
            User user = (User) response.getObject();
            String email = user.getEmail();
            CustomerService customerService =  context.getBean(CustomerService.class);
            Customer customer = customerService.findByEmail(email);
            if(customer != null && customer.getStatus().equals(Status.APPROVE)){
                CustomerManager customerManager = context.getBean(CustomerManager.class);
                response.addErrors(customerManager.reject(customer,username).getErrors());
            }
        }
        return response;
    }

    @Override
    public OperationResponse findAll(SystemObject systemObject, String username) {
        OperationResponse response = super.findAll(systemObject, username);
        if(response.isValid()){
            List<UserDTO> userDTOList = super.toDTO((List<User>) response.getObject());
            response.setDataObject(userDTOList);
        }
        return response;
    }

    @Override
    public OperationResponse findAllUsable(StatusObject statusObject, String username) {
        OperationResponse response = super.findAllUsable(statusObject, username);
        if(response.isValid()){
            List<UserDTO> userDTOList = super.toDTO((List<User>) response.getObject());
            response.setDataObject(userDTOList);
        }
        return response;
    }

    public OperationResponse findNeedsApproval(String opUser, String username) {
        User user = new User();
        user.setUsername(opUser);
        OperationResponse response = super.findNeedsApproval(user, username);
        return response;
    }

    public OperationResponse remove(String opUser, String username){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(opUser);
        OperationResponse response = super.manageOperation(userDTO, Operation.REMOVE, username);
        return response;
    }

    public OperationResponse approve(String opUser, String username){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(opUser);
        OperationResponse response = super.manageOperation(userDTO, Operation.APPROVE, username);
        return response;
    }

    public OperationResponse reject(String opUser, String username){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(opUser);
        OperationResponse response = super.manageOperation(userDTO, Operation.REJECT, username);
        return response;
    }

    public OperationResponse filter(UserFilterDTO userFilterDTO){
        FilterService filterService = context.getBean(FilterService.class);
        List<UserDTO> filteredUsersDto= filterService.findFilteredUsers(userFilterDTO);
        OperationResponse response = new OperationResponse(filteredUsersDto);
        return response;
    }

    public OperationResponse changePassword(ChangePasswordDTO changePasswordDTO){
        OperationResponse response = validateInput(changePasswordDTO);
        if(response.isValid()){
            User user = (User) response.getObject();
            LoginService loginService = context.getBean(LoginService.class);
            response.addErrors(loginService.login(user.getUsername(), changePasswordDTO.getOldPassword()).getErrors());
            if(response.isValid()){
                PasswordValidator passwordValidator = new PasswordValidator();
                response.addErrors(passwordValidator.validate(changePasswordDTO.getNewPassword()));
                if(response.isValid()){
                    PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
                    user.setPassword(passwordAuthentication.hash(changePasswordDTO.getNewPassword().toCharArray()));
                    ManagerUtils managerUtils = context.getBean(ManagerUtils.class);
                    UserService userService = (UserService) managerUtils.getService(new User());
                    userService.update(user);
                }
            }
            else {
                response.addError(new ErrorInfo(ErrorType.AUTHORIZATION_ERROR, "Current password is wrong"));
            }
        }
        response.setDataObject(changePasswordDTO);
        return response;
    }
    private OperationResponse validateInput(ChangePasswordDTO changePasswordDTO){
        OperationResponse response = new OperationResponse();
        if(changePasswordDTO == null || changePasswordDTO.getUsername() == null ||
                changePasswordDTO.getOldPassword() == null || changePasswordDTO.getNewPassword() == null){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Bad request"));
        }
        ManagerUtils managerUtils = context.getBean(ManagerUtils.class);
        UserService userService = (UserService) managerUtils.getService(new User());
        if(response.isValid()){
            User user = userService.findByUsername(changePasswordDTO.getUsername());
            if(user == null){
                response.addError(new ErrorInfo(ErrorType.NOT_FOUND_ERROR, "User not found"));
            }
            response.setDataObject(user);
        }
        return response;
    }
}
