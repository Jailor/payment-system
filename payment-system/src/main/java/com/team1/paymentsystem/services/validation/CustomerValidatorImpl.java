package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.repositories.CustomerRepository;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.team1.paymentsystem.states.Operation.CREATE;

@Service
public class CustomerValidatorImpl implements CustomerValidator{
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    EmailValidator emailValidator;
    @Autowired
    PhoneNumberValidator phoneNumberValidator;
    @Override
    public List<ErrorInfo> validate(Customer customer, Operation operation) {
        List<ErrorInfo> errors = new LinkedList<>();
        if(customer == null){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Customer is required"));
            return errors;
        }
        // validate Name
        String name = customer.getName();
        if(name == null || name.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Name is required"));
        }
        // validate address
        String address = customer.getAddress();
        if(address == null || address.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Address is required"));
        }
        // validate email
        String email = customer.getEmail();
        if(email == null || email.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Email is required"));
        }
        else{
            errors.addAll(emailValidator.validate(email));
            if(operation.equals(CREATE)){
                Customer customer1 = customerRepository.findByEmail(email).orElse(null);
                if(customer1 != null){
                    errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Email is already taken"));
                }
            }
            else
            {
                Customer customer1 = customerRepository.findByEmail(email).orElse(null);
                if(customer1 == null){
                    errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Email is not found"));
                }
            }
        }
        // validate phone number
        String phoneNumber = customer.getPhoneNumber();
        if(phoneNumber == null || phoneNumber.equals("")){
            errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Phone number is required"));
        }
        else{
            errors.addAll(phoneNumberValidator.validate(phoneNumber));
            if(operation.equals(CREATE)){
                Customer customer1 = customerRepository.findByPhoneNumber(phoneNumber).orElse(null);
                if(customer1 != null){
                    errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Phone number is already taken"));
                }
            }
            else{
                Customer customer1 = customerRepository.findByPhoneNumber(phoneNumber).orElse(null);
                if(customer1 == null){
                    errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,"Phone number is not found"));
                }
            }
        }
        return errors;
    }
}
