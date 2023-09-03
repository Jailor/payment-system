package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.entities.history.CustomerHistory;

import java.util.List;

public interface CustomerHistoryService extends HistoryService<Customer, CustomerHistory> {
    CustomerHistory createHistory(Customer customer);
    CustomerHistory save(CustomerHistory customerHistory);
    List<CustomerHistory> findAll();
    CustomerHistory findById(long id);
}
