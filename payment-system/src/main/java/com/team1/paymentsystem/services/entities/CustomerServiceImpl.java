package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.mappers.entity.CustomerMapper;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.repositories.AuditRepository;
import com.team1.paymentsystem.repositories.CustomerRepository;
import com.team1.paymentsystem.services.validation.CustomerValidator;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CustomerServiceImpl implements CustomerService{
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    protected AuditRepository auditRepository;
    @Autowired
    protected CustomerValidator customerValidator;
    @Override
    public Customer save(Customer customer) {
        Customer fromDB = customerRepository.findById(customer.getId()).orElse(null);
        if(fromDB!=null){
            return null;
        }
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(Customer customer) {
        Customer fromDB = customerRepository.findById(customer.getId()).orElse(null);
        if(fromDB==null){
            return null;
        }
        BeanUtils.copyProperties(customer,fromDB);
        return customerRepository.save(fromDB);
    }

    @Override
    public Customer remove(long id) {
        Customer fromDB = customerRepository.findById(id).orElse(null);
        if(fromDB == null){
            return null;
        }
        customerRepository.delete(fromDB);
        Customer customer = new Customer();
        customer.setId(id);
        return customer;
    }

    @Override
    public Customer remove(Customer customer) {
        if(customer == null){
            return null;
        }
        customerRepository.delete(customer);
        return customer;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer findByDiscriminator(Customer obj) {
        return customerRepository.findByEmail(obj.getEmail()).orElse(null);
    }

    @Override
    public Customer toEntity(CustomerDTO customerDTO, Operation operation) {
        return customerMapper.toEntity(customerDTO, operation);
    }

    @Override
    public List<Customer> findNeedsApproval() {
        return customerRepository.findNeedsApproval();
    }

    @Override
    public CustomerDTO toDTO(Customer entity) {
        return customerMapper.toDTO(entity);
    }

    @Override
    public Customer makeCopy(Customer obj) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(obj,customer);
        return customer;
    }

    @Override
    public List<ErrorInfo> validate(Customer obj, Operation operation) {
        return customerValidator.validate(obj,operation);
    }

    @Override
    public boolean fourEyesCheck(Customer obj, String username, int nEyesCheck) {
        int limit = nEyesCheck/2 - 1;
        List<Audit> audits = auditRepository.findUsersWhichApprovedCheck(obj.getId(),"CUSTOMER");
        if(audits.size() > limit){
            audits = audits.subList(0,limit);
        }
        for(Audit audit : audits){
            if(audit.getUser().getUsername().equals(username)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Customer findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }
}
