package com.team1.paymentsystem.managers;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.dto.filter.CustomerFilterDTO;
import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.managers.common.OperationManager;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.FilterService;
import com.team1.paymentsystem.services.entities.CustomerService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
@Log
public class CustomerManager extends OperationManager<Customer, CustomerDTO> {
    @Override
    public OperationResponse findAll(SystemObject systemObject, String username) {
        OperationResponse response = super.findAll(systemObject, username);
        if(response.isValid()){
            List<CustomerDTO> customerDTOList = super.toDTO((List<Customer>) response.getObject());
            response.setDataObject(customerDTOList);
        }
        return response;
    }
    public OperationResponse filter(CustomerFilterDTO customerFilterDTO){
        FilterService filterService = context.getBean(FilterService.class);
        List<CustomerDTO> filteredCustomersDto = filterService.findFilteredCustomer(customerFilterDTO);
        OperationResponse response = new OperationResponse(filteredCustomersDto);
        return response;
    }
    public OperationResponse findByPhoneNumber(String phoneNumber){
        CustomerService customerService =context.getBean(CustomerService.class);
        Customer customer = customerService.findByPhoneNumber(phoneNumber);
        OperationResponse response = new OperationResponse();
        if(customer != null){
            response.setDataObject(customer);
        }else {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Customer with this phone number not found"));
        }
        return response;

    }
}
