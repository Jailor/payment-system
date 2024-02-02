package com.team1.paymentsystem.managers.common;

import com.team1.paymentsystem.dto.AuditDTO;
import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.entities.*;
import com.team1.paymentsystem.entities.common.AbstractPayment;
import com.team1.paymentsystem.entities.common.StatusObject;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.entities.history.*;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.mappers.*;
import com.team1.paymentsystem.mappers.approve.*;
import com.team1.paymentsystem.mappers.entity.*;
import com.team1.paymentsystem.services.entities.*;
import com.team1.paymentsystem.services.entities.history.*;
import com.team1.paymentsystem.states.ApplicationConstants;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.Status;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional(rollbackFor = Exception.class)
@Log
public class ManagerUtils {
    @Autowired
    ApplicationContext context;

    @Autowired
    ApplicationConstants applicationConstants;

    /**
     * used to map to dto and vice versa, extensible using the getMapper method
     * @param entity - the entity to be converted
     * @return the DTO representation of the entity
     */
    public SystemDTO toDTO(SystemObject entity) {
        if(entity == null){
            return null;
        }
        Mapper mapper = getMapper(entity);
        return mapper.toDTO(entity);
    }

    public SystemObject toEntity(SystemDTO dto, Operation operation) {
        if(dto == null){
            return null;
        }
        Mapper mapper = getMapper(dto);
        return mapper.toEntity(dto, operation);
    }

    public void fourEyesCheck(GeneralService generalService, OperationResponse operationResponse,
                           String username, StatusObject returnObject){
        if(operationResponse.isValid()){
            boolean ok = generalService.fourEyesCheck(returnObject, username,4);
            if(!ok){
                log.severe("Four eyes check failed for approval " + username + " " + returnObject.getClass().getSimpleName());
                if(applicationConstants.N_EYES){
                    operationResponse.addError(new ErrorInfo(ErrorType.FOUR_EYES_ERROR,
                            "Four eyes check failed for approval " + username +
                                    " and object " + returnObject.getClass().getSimpleName()));
                }
            }
        }
    }

    public void nEyesCheckPayment(Payment payment, String username, OperationResponse response){
        if(response.isValid() && !username.equals("mobile")){
            PaymentService paymentService = context.getBean(PaymentService.class);
            // n eyes check
            int eyes = paymentService.getEyes(payment);
            boolean ok = paymentService.fourEyesCheck(payment, username,eyes);
            if(!ok){
                log.severe(eyes + " eyes check failed for approval payment "
                        + username + " " + payment.getSystemReference());
                if(applicationConstants.N_EYES) {
                    response.addError(new ErrorInfo(ErrorType.FOUR_EYES_ERROR,
                            eyes + " eyes check failed for approval " + username +
                                    " and payment " + payment.getSystemReference()));
                }
            }
        }
    }


    /**
     * used to find the mapper associated with the given object,
     * extend by adding a mapper implementation and adding a case
     */
    protected Mapper getMapper(Object obj) {
        Mapper mapper = null;
        if (obj instanceof Profile || obj instanceof ProfileDTO) {
            mapper = context.getBean(ProfileMapper.class);
        } else if (obj instanceof User || obj instanceof UserDTO) {
            mapper = context.getBean(UserMapper.class);
        } else if (obj instanceof Audit || obj instanceof AuditDTO) {
            mapper = context.getBean(AuditMapper.class);
        } else if (obj instanceof Account || obj instanceof AccountDTO){
            mapper = context.getBean(AccountMapper.class);
        } else if (obj instanceof Customer || obj instanceof CustomerDTO){
            mapper = context.getBean(CustomerMapper.class);
        } else if (obj instanceof Payment || obj instanceof PaymentDTO) {
            mapper = context.getBean(PaymentMapper.class);
        }
        return mapper;
    }

    /**
     * Returns an instance of the history service associated with the
     * given object.
     * @param mock - the mock object to be used for instancing
     * @return the history service associated with the mock object
     */

    protected HistoryService getHistoryService(SystemObject mock) {
        HistoryService historyService = null;
        if (mock instanceof Profile) {
            historyService = context.getBean(ProfileHistoryService.class);
        } else if (mock instanceof User) {
            historyService = context.getBean(UserHistoryService.class);
        } else if (mock instanceof Account){
            historyService = context.getBean(AccountHistoryService.class);
        } else if (mock instanceof Customer){
            historyService = context.getBean(CustomerHistoryService.class);
        } else if(mock instanceof Payment){
            historyService = context.getBean(PaymentHistoryService.class);
        }
        return historyService;
    }

    /**
     * Generates a service of the type ApproveMapper associated with the given object.
     * ApproveMapper map from object to DTOs of both "old" and "new" version. Extend by adding
     * a mapper implementation and adding a case.
     */
    protected ApproveMapper getApproveMapper(Object obj){
        ApproveMapper mapper = null;
        if (obj instanceof Profile || obj instanceof ProfileDTO) {
            mapper = context.getBean(ProfileApproveMapper.class);
        } else if (obj instanceof User || obj instanceof UserDTO) {
            mapper = context.getBean(UserApproveMapper.class);
        } else if(obj instanceof Account || obj instanceof AccountDTO){
            mapper = context.getBean(AccountApproveMapper.class);
        } else if(obj instanceof Customer || obj instanceof CustomerDTO){
            mapper = context.getBean(CustomerApproveMapper.class);
        }
        return mapper;
    }

    /**
     * Used by the operation manager to instantiate the correct service implementation for a given
     * object. Extend by adding a service implementation and adding a case.
     * @param systemObject - the object to be used for instancing the service
     * @return the service associated with the given object in the GeneralService interface
     */
    public GeneralService getService(SystemObject systemObject) {
        GeneralService generalService = null;
        if (systemObject instanceof Profile) {
            generalService = context.getBean(ProfileService.class);
        } else if (systemObject instanceof User) {
            generalService = context.getBean(UserService.class);
        } else if (systemObject instanceof Account){
            generalService = context.getBean(AccountService.class);
        } else if(systemObject instanceof Customer){
            generalService = context.getBean(CustomerService.class);
        } else if (systemObject instanceof Payment){
            generalService = context.getBean(PaymentService.class);
        }
        return generalService;
    }

    /**
     * Returns the class name. Extend by adding a case and adding a string representation of the class name
     * @param systemObject - the object to be used for persistence
     * @return the string representation of the class name of the given object
     * @implNote see if it can be replaced with a simple .getClass().getSimpleName()
     */
    protected String getClassName(SystemObject systemObject) {
        if (systemObject instanceof Profile) {
            return "PROFILE";
        } else if (systemObject instanceof User) {
            return "USER";
        } else if (systemObject instanceof Audit) {
            return "AUDIT";
        } else if (systemObject instanceof Account){
            return "ACCOUNT";
        } else if(systemObject instanceof Customer){
            return "CUSTOMER";
        } else if (systemObject instanceof AbstractPayment){
            return "PAYMENT";
        }
        return null;
    }

    /**
     * Given an object, created the associated history. The history service must implement the
     * createHistory method. Extend by adding a case and adding a history service implementation
     * @param systemObject - the object to be used for creating the history object
     * @return the history object created by the history service associated with the given object
     */
    public SystemObject insertHistoryObj(SystemObject systemObject) {
        SystemObject historyObject = null;
        if (systemObject instanceof Profile) {
            HistoryService<Profile, ProfileHistory> profileHistoryService = context.getBean(ProfileHistoryService.class);
            ProfileHistory profileHistory = profileHistoryService.createHistory((Profile) systemObject);
            historyObject = profileHistoryService.save(profileHistory);
        } else if (systemObject instanceof User) {
            HistoryService<User, UserHistory> userHistoryService = context.getBean(UserHistoryService.class);
            UserHistory userHistory = userHistoryService.createHistory((User) systemObject);
            historyObject = userHistoryService.save(userHistory);
        } else if (systemObject instanceof Account){
            HistoryService<Account, AccountHistory> accountHistoryService = context.getBean(AccountHistoryService.class);
            AccountHistory accountHistory = accountHistoryService.createHistory((Account) systemObject);
            historyObject = accountHistoryService.save(accountHistory);
        } else if (systemObject instanceof Customer){
            HistoryService<Customer, CustomerHistory> customerHistoryService= context.getBean(CustomerHistoryService.class);
            CustomerHistory customerHistory = customerHistoryService.createHistory((Customer) systemObject);
            historyObject = customerHistoryService.save(customerHistory);
        } else if (systemObject instanceof Payment){
            HistoryService<Payment, PaymentHistory> paymentHistoryService = context.getBean(PaymentHistoryService.class);
            PaymentHistory paymentHistory = paymentHistoryService.createHistory((Payment) systemObject);
            historyObject = paymentHistoryService.save(paymentHistory);
        }
        return historyObject;
    }

    /**
     * Given an object, find the associated next state in history.
     * next state id. Extend by adding a case and adding a history service implementation.
     * @param statusObject - the object to be used for finding the history object
     * @return the history object found by the history service associated with the given object
     */
    public StatusObject findHistoryObjByNextStateId(StatusObject statusObject) {
        StatusObject historyObject = null;
        if (statusObject instanceof Profile) {
            HistoryService<Profile, ProfileHistory> profileHistoryService = context.getBean(ProfileHistoryService.class);
            historyObject = profileHistoryService.findById(statusObject.getNextStateId());
        } else if (statusObject instanceof User) {
            HistoryService<User, UserHistory> userHistoryService = context.getBean(UserHistoryService.class);
            historyObject = userHistoryService.findById(statusObject.getNextStateId());
        } else if (statusObject instanceof Account){
            HistoryService<Account, AccountHistory> accountHistoryService = context.getBean(AccountHistoryService.class);
            historyObject = accountHistoryService.findById(statusObject.getNextStateId());
        }  else if (statusObject instanceof Customer){
            HistoryService<Customer,CustomerHistory> customerHistoryService = context.getBean(CustomerHistoryService.class);
            historyObject = customerHistoryService.findById(statusObject.getNextStateId());
        }
        return historyObject;
    }

    protected Operation findOperationByStatus(Status currentStatus, Status nextStatus) {
        Operation operation = null;
        if (currentStatus.equals(Status.APPROVE) && nextStatus.equals(Status.ACTIVE)) {
            operation = Operation.CREATE;
        } else if (currentStatus.equals(Status.ACTIVE) && nextStatus.equals(Status.ACTIVE)) {
            operation = Operation.MODIFY;
        } else if (currentStatus.equals(Status.BLOCKED) && nextStatus.equals(Status.BLOCKED)) {
                operation = Operation.MODIFY;
        } else if (currentStatus.equals(Status.ACTIVE) && nextStatus.equals(Status.BLOCKED)) {
            operation = Operation.BLOCK;
        } else if ((currentStatus.equals(Status.ACTIVE) || currentStatus.equals(Status.BLOCKED))
                && nextStatus.equals(Status.REMOVED)) {
            operation = Operation.REMOVE;
        } else if (currentStatus.equals(Status.BLOCKED) && nextStatus.equals(Status.ACTIVE)) {
            operation = Operation.UNBLOCK;
        }
        return operation;
    }

}
