package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.entities.history.CustomerHistory;
import com.team1.paymentsystem.repositories.history.CustomerHistoryRepository;
import com.team1.paymentsystem.services.entities.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerHistoryServiceImpl implements CustomerHistoryService{
    @Autowired
    protected CustomerHistoryRepository customerHistoryRepository;
    @Autowired
    protected CustomerService customerService;
    @Override
    public CustomerHistory createHistory(Customer customer) {
        CustomerHistory customerHistory = new CustomerHistory();
        BeanUtils.copyProperties(customer,customerHistory);
        customerHistory.setId(0);
        customerHistory.setOriginalId(customer.getId());
        customerHistory.setTimeStamp(LocalDateTime.now());
        System.err.println(customer.getDefaultAccountNumber());
        System.err.println(customerHistory.getDefaultAccountNumber());
        return customerHistory;
    }

    @Override
    public CustomerHistory save(CustomerHistory customerHistory) {
        return customerHistoryRepository.save(customerHistory);
    }

    @Override
    public List<CustomerHistory> findAll() {
        return customerHistoryRepository.findAll();
    }

    @Override
    public List<CustomerHistory> findByOriginalId(long originalId) {
        return customerHistoryRepository.findByOriginalId(originalId).orElse(null);
    }

    @Override
    public CustomerHistory findById(long id) {
        return customerHistoryRepository.findById(id).orElse(null);
    }
}
