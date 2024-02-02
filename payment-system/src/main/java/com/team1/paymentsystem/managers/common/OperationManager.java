package com.team1.paymentsystem.managers.common;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.entities.common.StatusObject;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.mappers.approve.ApproveMapper;
import com.team1.paymentsystem.services.entities.AuditService;
import com.team1.paymentsystem.services.entities.GeneralService;
import com.team1.paymentsystem.services.entities.UserService;
import com.team1.paymentsystem.services.entities.history.HistoryService;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.Status;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.java.Log;
import org.hibernate.StaleStateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.team1.paymentsystem.states.Operation.*;


@Component
@Transactional(rollbackFor = Exception.class)
@Log
@Primary
public class OperationManager<T extends StatusObject, S extends SystemDTO> {
    @Autowired
    protected ApplicationContext context;
    @Autowired
    protected ManagerUtils managerUtils;
    @Autowired
    private AuthorizationManager authorizationManager;

    /**
     * Saves a new status object with the provided information. Authorization, validation, and audit creation are performed.
     * The object is transitioned to the "APPROVE" state for pending approval.
     *
     * @param statusObject The status object to be saved.
     * @param username The username of the user performing the operation.
     * @return The operation response, containing the saved object on success, or error information on failure.
     */
    public OperationResponse save(T statusObject, String username) {
        OperationResponse operationResponse = authorizationManager.authorizeProfile(statusObject, username, CREATE);
        if (!operationResponse.isValid()) return operationResponse;

        GeneralService<StatusObject, SystemDTO> generalService = managerUtils.getService(statusObject);
        operationResponse.addErrors(generalService.validate(statusObject, CREATE));
        if(operationResponse.isValid()){
            statusObject.setStatus(Status.APPROVE);
            statusObject.setNextStatus(Status.ACTIVE);
            StatusObject returnObject = generalService.save(statusObject);
            if(returnObject == null){
                operationResponse.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                        "Error saving the object: the object already exists"));
            }
            if(operationResponse.isValid()){
                StatusObject historyObj = (StatusObject) managerUtils.insertHistoryObj(returnObject);
                statusObject.setNextStateId(historyObj.getId());
                // UPDATE with nextStateId
                returnObject = generalService.update(statusObject);
                createAudit(returnObject, username, CREATE);
                operationResponse.setDataObject(returnObject);
            }
        }
        return operationResponse;
    }

    /**
     * Updates an existing status object with new information. Authorization, validation, and audit creation are performed.
     * If the entity is not in active or blocked status, the update is rejected.
     *
     * @param statusObject The status object to be updated.
     * @param username The username of the user performing the operation.
     * @return The operation response, containing the updated object on success, or error information on failure.
     */
    public OperationResponse update(T statusObject, String username) {
        GeneralService<StatusObject, SystemDTO> generalService = managerUtils.getService(statusObject);

        OperationResponse response= authorizeAndCheckStatus(statusObject, username, MODIFY);
        StatusObject returnObject = (StatusObject) response.getObject();

        if(response.isValid()){
            List<Status> validStatuses = List.of(Status.ACTIVE, Status.BLOCKED);
            if(!validStatuses.contains(returnObject.getStatus())){
                response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                        "Entity is not active"));
                return response;
            }
            StatusObject copyObject = generalService.makeCopy(returnObject);

            // validate
            response.addErrors(generalService.validate(statusObject, Operation.MODIFY));

            if(response.isValid()) {
                // copy properties to insert into history
                Status originalStatus = returnObject.getStatus();
                BeanUtils.copyProperties(statusObject, returnObject);
                returnObject.setStatus(Status.APPROVE);
                returnObject.setNextStatus(originalStatus);
                returnObject.setId(copyObject.getId());
                returnObject.setVersion(copyObject.getVersion());
                StatusObject historyObj = (StatusObject) managerUtils.insertHistoryObj(returnObject);

                // add nextStateId
                copyObject.setNextStateId(historyObj.getId());
                copyObject.setNextStatus(originalStatus);

                // update and audit
                copyObject = generalService.update(copyObject);
                createAudit(copyObject, username, MODIFY);

                response.setDataObject(copyObject);
            }
        }


        return response;
    }

    /**
     * Removes a status object. Authorization, validation, and audit creation are performed.
     * If the object is an account, it must be in the "CLOSED" status to be removed.
     *
     * @param statusObject The status object to be removed.
     * @param username The username of the user performing the operation.
     * @return The operation response, containing the removed object on success, or error information on failure.
     */
    public OperationResponse remove(T statusObject, String username) {
        GeneralService<StatusObject, SystemDTO> generalService = managerUtils.getService(statusObject);
        OperationResponse response = authorizeAndCheckStatus(statusObject, username, REMOVE);
        StatusObject returnObject = (StatusObject) response.getObject();

        if(response.isValid()){
            Status originalStatus = returnObject.getStatus();
            returnObject.setStatus(Status.APPROVE);
            returnObject.setNextStatus(Status.REMOVED);

            StatusObject historyObj = (StatusObject) managerUtils.insertHistoryObj(returnObject);
            returnObject.setNextStateId(historyObj.getId());
            returnObject.setStatus(originalStatus);
            returnObject.setNextStatus(Status.REMOVED);

            returnObject = generalService.update(returnObject);
            createAudit(returnObject, username, REMOVE);

            response.setDataObject(returnObject);
        }
        return response;
    }

    /**
     * Approves a pending status object. Authorization, validation, four-eyes check, and audit creation are performed.
     * The object is transitioned to the next state specified in its history.
     *
     * @param statusObject The status object to be approved.
     * @param username The username of the user performing the operation.
     * @return The operation response, containing the approved object on success, or error information on failure.
     */
    public OperationResponse approve(T statusObject, String username){
        GeneralService<StatusObject, SystemDTO> generalService = managerUtils.getService(statusObject);
        OperationResponse response = authorizeAndCheckStatus(statusObject, username, APPROVE);
        StatusObject returnObject = (StatusObject) response.getObject();

        if(response.isValid()){
            managerUtils.fourEyesCheck(generalService, response, username, returnObject);
            if(response.isValid()){
                StatusObject historyObj = managerUtils.findHistoryObjByNextStateId(returnObject);
                // copy properties since we are doing approve
                long originalId = returnObject.getId();
                long originalVersion = returnObject.getVersion();
                BeanUtils.copyProperties(historyObj, returnObject);
                returnObject.setId(originalId);
                returnObject.setVersion(originalVersion);
                // set correct status
                returnObject.setStatus(historyObj.getNextStatus());
                returnObject.setNextStatus(null);
                returnObject.setNextStateId(null);
                // save the object
                returnObject = generalService.update(returnObject);
                // history and audit
                managerUtils.insertHistoryObj(returnObject);
                createAudit(returnObject, username, APPROVE);
            }
            response.setDataObject(returnObject);
        }
        return response;
    }

    /**
     * Rejects the operation pending on the object. Authorization, validation and fourEyesCheck are performed.
     * A history and audit are also created.
     * @param statusObject object to reject
     * @param username username of the user performing the operation
     * @return the operation response, with the rejected object as data object
     */
    public OperationResponse reject(T statusObject, String username){
        GeneralService<StatusObject, SystemDTO> generalService = managerUtils.getService(statusObject);
        OperationResponse response = authorizeAndCheckStatus(statusObject, username, REJECT);
        StatusObject returnObject = (StatusObject) response.getObject();
        if(response.isValid()){
            managerUtils.fourEyesCheck(generalService, response, username, returnObject);
            if(response.isValid()){
                // objects that have just been created are in APPROVE state
                // a reject on an object in APPROVE state means that the object is REMOVED
                if(returnObject.getStatus().equals(Status.APPROVE)){
                    returnObject.setStatus(Status.REMOVED);
                }
                returnObject.setNextStateId(null);
                returnObject.setNextStatus(null);
                returnObject = generalService.update(returnObject);
                // history and audit
                managerUtils.insertHistoryObj(returnObject);
                createAudit(returnObject, username, REJECT);
            }
            response.setDataObject(returnObject);
        }
        return response;
    }

    /**
     * Validates an object and authorizes the operation for the given user.
     * @param statusObject object type that yields the appropriate entity for authorization
     * @param username username of the user performing the operation
     * @param operation operation to be performed
     * @return OperationResponse object with the appropriate errors and data
     */
    private OperationResponse authorizeAndCheckStatus(StatusObject statusObject, String username, Operation operation){
        StatusObject returnObject = null;
        OperationResponse response = authorizationManager.authorizeProfile(statusObject, username, operation);
        if (response.isValid()) {
            GeneralService generalService = managerUtils.getService(statusObject);
            returnObject = (StatusObject) generalService.findByDiscriminator(statusObject);
            if(returnObject == null) {
                response.addError(new ErrorInfo(ErrorType.NOT_FOUND_ERROR,
                        "Object has not been found  (by discriminator)"));
            }
            if(operation.equals(APPROVE) || operation.equals(REJECT)){
                if(returnObject != null && returnObject.getNextStateId() == null){
                    response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                            "Entity is not undergoing any modification"));
                }
            }
            else {
                if(returnObject != null && returnObject.getNextStateId() != null){
                    response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                            "Entity is already undergoing another modification"));
                }
            }

        }
        response.setDataObject(returnObject);
        return response;
    }


    public OperationResponse authorize(StatusObject statusObject, String username, Operation operation) {
        return authorizationManager.authorizeProfile(statusObject, username, operation);
    }


    public OperationResponse nEyesCheck(SystemObject obj, String username, int eyes) {
        OperationResponse response = new OperationResponse();
        managerUtils.fourEyesCheck(managerUtils.getService(obj), response, username, (StatusObject) obj);
        return response;
    }

    /**
     * @param systemObject - the object type that is being searched for
     * @param username - the username of the user that is searching
     * @return a list of all the objects of the given type, wrapped in an operation response
     */
    public OperationResponse findAll(SystemObject systemObject, String username) {
        OperationResponse operationResponse = authorizationManager.authorizeProfile(systemObject, username, LIST);
        if (!operationResponse.isValid()) return operationResponse;

        GeneralService<SystemObject, SystemDTO> generalService = managerUtils.getService(systemObject);
        List<SystemObject> returnList = generalService.findAll();
        operationResponse.setDataObject(returnList);

        return operationResponse;
    }

    /**
     * @param statusObject - the object type that is being searched for
     * @param username - the username of the user that is searching
     * @return a list of all the objects of the given type that are not in REMOVED, APPROVE or REPAIR state
     */
    public OperationResponse findAllUsable(StatusObject statusObject, String username) {
        OperationResponse operationResponse = authorizationManager.authorizeProfile(statusObject, username, LIST);
        if (!operationResponse.isValid()) return operationResponse;

        GeneralService<StatusObject, SystemDTO> generalService = managerUtils.getService(statusObject);
        List<StatusObject> returnList = generalService.findAll();
        List<StatusObject> goodList = new LinkedList<>();
        for(StatusObject obj : returnList) {
            if(!obj.getStatus().equals(Status.REMOVED)
                    && !obj.getStatus().equals(Status.APPROVE)
                    && !obj.getStatus().equals(Status.REPAIR))
            {
                goodList.add(obj);
            }
        }

        operationResponse.setDataObject(goodList);
        return operationResponse;
    }


    /**
     * For the entity type, given by the class of obj,
     * finds all the entities that need approval and wraps them in an approval DTO.
     * @param obj - mock object to find the correct services and mappers
     * @return - the object that needs approval, wrapped in an approval DTO
     */
    public OperationResponse findAllNeedsApproval(StatusObject obj, String username){
        OperationResponse response = authorizationManager.authorizeProfile(obj, username, LIST);
        if(response.isValid()){
            GeneralService generalService = managerUtils.getService(obj);
            HistoryService historyService = managerUtils.getHistoryService(obj);
            ApproveMapper mapper = managerUtils.getApproveMapper(obj);
            List<StatusObject> returnList = generalService.findNeedsApproval();
            List<SystemDTO> dtos = new LinkedList<>();
            for(StatusObject statusObject : returnList){
                long id = statusObject.getNextStateId();
                StatusObject historyObj = (StatusObject) historyService.findById(id);
                SystemDTO dto = (SystemDTO) mapper.toDTO(statusObject, historyObj,
                        managerUtils.findOperationByStatus(statusObject.getStatus(), statusObject.getNextStatus()));
                dtos.add(dto);
            }
            response.setDataObject(dtos);
        }

        return response;
    }

    public OperationResponse findById(StatusObject obj, String username) {
        GeneralService generalService = managerUtils.getService(obj);
        return new OperationResponse(generalService.findById(obj.getId()));
    }

    public OperationResponse findByDiscriminant(StatusObject obj, String username) {
        GeneralService generalService = managerUtils.getService(obj);
        OperationResponse response;
        try{
            SystemObject object = generalService.findByDiscriminator(obj);
            if(object != null){
                SystemDTO objectDTO = toDTO((T) object);
                response = new OperationResponse(objectDTO);
            }else{
                List<ErrorInfo> errors = new LinkedList<>();
                errors.add(new ErrorInfo(ErrorType.NOT_FOUND_ERROR, "Object not found"));
                response = new OperationResponse(null, errors);
            }
        }catch (Exception e){
            List<ErrorInfo> errors = new LinkedList<>();
            errors.add(new ErrorInfo(ErrorType.INTERNAL_ERROR, e.getMessage()));
            response = new OperationResponse(null, errors);
        }

        return response;
    }

    /**
     * For the givenDTO, generate the approvalDTO
     * @param obj - dto used to find the entity
     * @return - the object that needs approval, wrapped in an approval DTO
     */
    public OperationResponse findNeedsApproval(StatusObject obj, String username){
        OperationResponse response = authorizationManager.authorizeProfile(obj, username, LIST);
        GeneralService generalService = managerUtils.getService(obj);
        HistoryService historyService = managerUtils.getHistoryService(obj);
        ApproveMapper mapper = managerUtils.getApproveMapper(obj);
        StatusObject statusObject = (StatusObject) generalService.findByDiscriminator(obj);
        if(statusObject == null) {
            response.addError(new ErrorInfo(ErrorType.NOT_FOUND_ERROR,
                    "Object has not been found  (by discriminator)"));
        }
        if(response.isValid()){
            long id = statusObject.getNextStateId();
            StatusObject historyObj = (StatusObject) historyService.findById(id);
            SystemDTO dto = (SystemDTO) mapper.toDTO(statusObject, historyObj,
                    managerUtils.findOperationByStatus(statusObject.getStatus(), statusObject.getNextStatus()));
            response.setDataObject(dto);
        }

        return response;
    }

    /**
     * Manages certain operations with DTOs for cleaner code in the controller.
     * @param systemDTO the data transfer object
     * @param operation the operation to be performed, currently only supports simple
     *                  CREATE, MODIFY, REMOVE, APPROVE, REJECT, for listing choose
     *                  the other appropriate methods in the operation manager
     * @param username the username of the user performing the operation
     * @return the operation response, with any errors if applicable
     */
    public OperationResponse manageOperation(S systemDTO,
                                             Operation operation, String username){
        SystemObject mock = toEntity(systemDTO, operation);
        OperationResponse response = new OperationResponse();
        if(mock == null){
            response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, "The data" +
                    "sent could not be mapped to an entity. Please check the data sent."));
        }

        if(response.isValid()){
            try {
                if (operation.equals(CREATE)) response = save((T) mock, username);
                else if (operation.equals(MODIFY)) response = update((T) mock, username);
                else if (operation.equals(REMOVE)) response = remove((T) mock, username);
                else if (operation.equals(APPROVE)) response = approve((T) mock, username);
                else if (operation.equals(REJECT)) response = reject((T) mock, username);
            }
            catch (StaleStateException | OptimisticLockException e){
                response.addError(new ErrorInfo(ErrorType.VERSIONING_ERROR,
                        "The object has been modified by another user. Please refresh the page and try again."));
            }

            if(response.isValid()){
                SystemDTO backDTO = toDTO((T) response.getObject());
                if(backDTO==null){
                    response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, "The data" +
                            "sent could not be mapped to a DTO. Please check the data sent."));
                }
                response.setDataObject(backDTO);
            }
        }

        return response;

    }

    /**
     * used to map to dto and vice versa, extensible using the getMapper method
     * @param entity - the entity to be converted
     * @return the DTO representation of the entity
     */
    public S toDTO(T entity) {
        Mapper mapper = managerUtils.getMapper(entity);
        return (S) mapper.toDTO(entity);
    }

    public List toDTO(List<T> entities) {
        List dtos = entities.stream().map(this::toDTO).collect(Collectors.toList());
        return dtos;
    }

    public T toEntity(SystemDTO dto, Operation operation) {
        Mapper mapper = managerUtils.getMapper(dto);
        return (T) mapper.toEntity(dto, operation);
    }

    public OperationResponse makeCopy(StatusObject entity) {
        GeneralService generalService = managerUtils.getService(entity);
        return new OperationResponse(generalService.makeCopy(entity));
    }


    public OperationResponse createAudit(SystemObject systemObject, String username, Operation operation) {
        Audit audit = new Audit();
        UserService userService = context.getBean(UserService.class);
        User user = userService.findByUsername(username);
        audit.setUser(user);
        audit.setOperation(operation);
        audit.setTimeStamp(LocalDateTime.now());
        audit.setClassName(managerUtils.getClassName(systemObject));
        audit.setObjectId(systemObject.getId());
        AuditService auditService = context.getBean(AuditService.class);
        Audit ret = auditService.save(audit);
        return new OperationResponse(ret);
    }

}