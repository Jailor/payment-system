package com.team1.paymentsystem.managers;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.dto.register.RegisterDTO;
import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.repositories.CustomerRepository;
import com.team1.paymentsystem.services.validation.PhoneNumberValidator;
import com.team1.paymentsystem.states.Operation;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
@Log
public class RegisterManager {
    @Autowired
    UserManager userManager;
    @Autowired
    CustomerManager customerManager;
    @Autowired
    ApplicationContext context;
    public OperationResponse save(RegisterDTO registerDTO) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(registerDTO,userDTO);
        userDTO.setProfileName("Customer");
        CustomerRepository customerRepository = context.getBean(CustomerRepository.class);
        OperationResponse response = new OperationResponse();
        PhoneNumberValidator phoneNumberValidator = context.getBean(PhoneNumberValidator.class);
        if(customerRepository.findByPhoneNumber(registerDTO.getPhoneNumber()).isEmpty()) {
            if(phoneNumberValidator.validate(registerDTO.getPhoneNumber()).isEmpty()){
                response = userManager.manageOperation(userDTO, Operation.CREATE, "mobile");
                if (response.isValid()) {
                    CustomerDTO customerDTO = new CustomerDTO();
                    BeanUtils.copyProperties(registerDTO, customerDTO);
                    customerDTO.setName(registerDTO.getFullName());
                    response.addErrors(customerManager.manageOperation(customerDTO, Operation.CREATE, "mobile").getErrors());
                }
            }
            else{
                response.addErrors( phoneNumberValidator.validate(registerDTO.getPhoneNumber()));
            }

        }
        else{
                response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Phone number is already taken"));
        }
        return response;
    }
}
