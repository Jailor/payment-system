package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.dto.filter.CustomerFilterDTO;
import com.team1.paymentsystem.entities.Customer;
import com.team1.paymentsystem.managers.CustomerManager;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.states.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.team1.paymentsystem.controllers.CommonUtils.getUsername;

@RestController
@RequestMapping("api/customer")
public class CustomerController {
    @Autowired
    CustomerManager customerManager;
    @PostMapping
    public @ResponseBody ResponseEntity<OperationResponse> save(@RequestBody CustomerDTO customerDTO, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = customerManager.manageOperation(customerDTO, Operation.CREATE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public @ResponseBody ResponseEntity<OperationResponse> update(@RequestBody CustomerDTO customerDTO, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = customerManager.manageOperation(customerDTO, Operation.MODIFY, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{email}")
    public @ResponseBody ResponseEntity<OperationResponse> delete(@PathVariable String email, HttpServletRequest request) {
        String username = getUsername(request);
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setEmail(email);
        OperationResponse response = customerManager.manageOperation(customerDTO, Operation.REMOVE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = customerManager.findAll(new Customer(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval/{email}")
    public @ResponseBody ResponseEntity<OperationResponse> findApprovalByName(@PathVariable String email, HttpServletRequest request) {
        String username = getUsername(request);
        Customer customer = new Customer();
        customer.setEmail(email);
        OperationResponse response = customerManager.findNeedsApproval(customer, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval")
    public @ResponseBody ResponseEntity<OperationResponse> findAllApprovalCustomers(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = customerManager.findAllNeedsApproval(new Customer(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public @ResponseBody ResponseEntity<OperationResponse> findByEmail(@PathVariable String email, HttpServletRequest request) {
        String username = getUsername(request);
        Customer customer = new Customer();
        customer.setEmail(email);
        OperationResponse response = customerManager.findByDiscriminant(customer, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("find-by-phone/{phoneNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findByPhoneNumber(@PathVariable String phoneNumber, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = customerManager.findByPhoneNumber(phoneNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("approve/{email}")
    public @ResponseBody ResponseEntity<OperationResponse> approve(@PathVariable String email, HttpServletRequest request) {
        String username = getUsername(request);
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setEmail(email);
        OperationResponse response = customerManager.manageOperation(customerDTO, Operation.APPROVE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("reject/{email}")
    public @ResponseBody ResponseEntity<OperationResponse> reject(@PathVariable String email, HttpServletRequest request) {
        String username = getUsername(request);
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setEmail(email);
        OperationResponse  response = customerManager.manageOperation(customerDTO, Operation.REJECT, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public @ResponseBody ResponseEntity<OperationResponse> findFilteredCustomer(@RequestBody CustomerFilterDTO customerFilterDTO) {
        OperationResponse response = customerManager.filter(customerFilterDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
