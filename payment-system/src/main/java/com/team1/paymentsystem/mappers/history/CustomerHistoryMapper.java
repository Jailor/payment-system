package com.team1.paymentsystem.mappers.history;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.dto.customer.CustomerHistoryDTO;
import com.team1.paymentsystem.mappers.entity.CustomerMapper;
import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.entities.history.CustomerHistory;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CustomerHistoryMapper implements Mapper<CustomerHistoryDTO, CustomerHistory> {
    @Autowired
    CustomerMapper customerMapper;
    @Override
    public CustomerHistoryDTO toDTO(CustomerHistory entity) {
        CustomerHistoryDTO customerHistoryDTO = new CustomerHistoryDTO();
        BeanUtils.copyProperties(entity,customerHistoryDTO);
        customerHistoryDTO.setStringTimeStamp(entity.getTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return customerHistoryDTO;
    }

    @Override
    public CustomerHistory toEntity(CustomerHistoryDTO dto, Operation operation) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(dto,customerDTO);
        Customer customer = customerMapper.toEntity(customerDTO, operation);
        CustomerHistory customerHistory = new CustomerHistory();
        BeanUtils.copyProperties(dto,customerHistory);
        customerHistory.setId(0);
        customerHistory.setOriginalId(customer.getId());
        if(customerHistory.getTimeStamp() == null) {
            customerHistory.setTimeStamp(LocalDateTime.now());
        }
        return customerHistory;
    }
}
