package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.entities.Customer;

import java.util.List;

public interface CustomerService extends GeneralService<Customer,CustomerDTO>{
    Customer save(Customer customer);
    Customer update(Customer customer);
    Customer remove(long id);
    Customer remove (Customer customer);
    List<Customer> findAll();
    Customer findById(long id);
    Customer findByEmail(String email);
    Customer findByPhoneNumber(String phoneNumber);
}
