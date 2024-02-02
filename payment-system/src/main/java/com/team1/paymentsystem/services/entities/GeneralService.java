package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.states.Operation;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface GeneralService<T extends SystemObject, DTO extends SystemDTO>{
    /**
     * Saves an object to the database. If the object already exists, null is returned.
     * @param obj - object to be saved
     * @return the object that was saved (with id), or null if it already exists
     */
    T save(T obj);

    /**
     * Updates an object in the database. If the object does not exist, null is returned.
     * @param obj - object to be updated
     * @return the object that was updated, or null if it does not exist
     */
    T update(T obj);

    /**
     * Removes an object from the database. If the object does not exist, null is returned.
     * @param obj - object to be removed
     * @return the object that was removed, or null if it does not exist
     */
    T remove(T obj);

    /**
     * @return a list of all objects of type specified by the service
     */
    List<T> findAll();

    /**
     * Finds an object by its id.
     * @param id - id of the object to be found
     * @return the object that was found, or null if it was not found
     */
    T findById(long id);

    /**
     * Takes in an object that contains a property something
     * that uniquely identifies the object. This method is used to find an object that is not
     * uniquely identified by its id. For example, if we have a class Person that has a property
     * name, we can use this method to find a person by name.
     * @param obj - partial object to be found that contains discriminator
     * @return the object that was found, or null if it was not found
     */
    T findByDiscriminator(T obj);

    /**
     * Converts a DTO to an entity. Services that implement this method are recommended to
     * delegate the conversion to the appropriate converter or mapper.
     * @param dto - DTO to be converted to entity
     * @param operation - the operation that is being performed on the object
     * @return entity representation of the DTO, or null if the conversion is not possible
     */
    T toEntity(DTO dto, Operation operation);

    /**
     * Finds all objects that need approval. What this means is different for each system object, but in general
     * this method refers to the APPROVE status.
     * @return a list of all objects that need approval
     */
    List<T> findNeedsApproval();

    /**
     * Converts an entity to a DTO. Services that implement this method are recommended to
     * delegate the conversion to the appropriate converter or mapper.
     * @param entity - entity to be converted to DTO
     * @return DTO representation of the entity, or null if the conversion is not possible
     */
    DTO toDTO(T entity);

    /**
     * Performs a DEEP copy of the object. The object does not have to be cloneable.
     * @param obj - object to be copied
     * @return a deep copy of the object
     */
    @NotNull
    T makeCopy(T obj);

    /**
     * Validates an object according to the business rules of the system, entity that is being validated
     * and the operation that is being performed. Services that implement this method are recommended to
     * delegate the validation to the appropriate validator.
     * @param obj - object to be validated
     * @param operation - the operation that is being performed on the object
     * @return a list of errors that occurred during validation, or an empty list if there are none
     */
    List<ErrorInfo> validate(T obj, Operation operation);

    /**
     * Checks an object for the n eyes principle, given by the parameter nEyesCheck.
     * The value of it should be even.
     * @param obj - object to be checked for four eyes principle
     * @param username - the username of the user that is trying to approve the object
     * @return true if the user is allowed to approve the object, false otherwise
     */
    boolean fourEyesCheck(T obj, String username, int nEyesCheck);
}
