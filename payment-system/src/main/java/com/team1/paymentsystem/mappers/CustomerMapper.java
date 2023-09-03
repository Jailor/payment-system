package com.team1.paymentsystem.mappers;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.repositories.CustomerRepository;
import com.team1.paymentsystem.services.entities.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper implements Mapper<CustomerDTO, Customer> {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ApplicationContext context;
    @Override
    public CustomerDTO toDTO(Customer entity) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(entity,customerDTO);
        customerDTO.setNeedsApproval(entity.getNextStateId() != null);
        if(entity.getDefaultAccountNumber()!=null){
            customerDTO.setDefaultAccountNumber(entity.getDefaultAccountNumber());
        }
        return customerDTO;
    }

    @Override
    public Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto,customer);
        Customer db = customerRepository.findByEmail(dto.getEmail()).orElse(new Customer());
        customer.setId(db.getId());
        if(dto.getDefaultAccountNumber() != null){
            AccountService accountService = context.getBean(AccountService.class);
            Account account = accountService.findByAccountNumber(dto.getDefaultAccountNumber());
            customer.setDefaultAccountNumber(account.getAccountNumber());
        }
        else{
            customer.setDefaultAccountNumber(db.getDefaultAccountNumber());
        }
        return customer;
    }
}
