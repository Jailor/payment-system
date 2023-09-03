package com.team1.paymentsystem.controllers.history;

import com.team1.paymentsystem.dto.customer.CustomerHistoryDTO;
import com.team1.paymentsystem.mappers.history.CustomerHistoryMapper;
import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.entities.history.CustomerHistory;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.history.CustomerHistoryService;
import com.team1.paymentsystem.services.entities.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/customer-history")
public class CustomerHistoryController {
    @Autowired
    CustomerHistoryService customerHistoryService;
    @Autowired
    CustomerService customerService;
    @Autowired
    CustomerHistoryMapper customerHistoryMapper;
    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(){
        List<CustomerHistory> customerHistories = customerHistoryService.findAll();
        List<CustomerHistoryDTO> dtos = customerHistories.stream().map(customerHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }
    @GetMapping("{email}")
    public @ResponseBody ResponseEntity<OperationResponse> findCustomerHistory(@PathVariable String email){
        Customer customer = customerService.findByEmail(email);
        List<CustomerHistory> customerHistories = customerHistoryService.findByOriginalId(customer.getId());
        List<CustomerHistoryDTO> dtos = customerHistories.stream().map(customerHistoryMapper::toDTO).toList();
        if(!dtos.isEmpty()){
            return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
        } else {
            List<ErrorInfo> errorInfos = List.of(new ErrorInfo(ErrorType.NOT_FOUND_ERROR,"User history not found"));
            OperationResponse operationResponse = new OperationResponse(errorInfos);
            return new ResponseEntity<>(operationResponse,HttpStatus.NOT_FOUND);
        }
    }


}
