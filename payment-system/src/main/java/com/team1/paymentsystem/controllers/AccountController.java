package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.dto.OperationDTO;
import com.team1.paymentsystem.dto.filter.AccountFilterDTO;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.managers.AccountManager;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.states.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.team1.paymentsystem.controllers.CommonUtils.getUsername;

@RestController
@RequestMapping("api/account")
public class AccountController {
    @Autowired
    AccountManager accountManager;

   @PostMapping
   public @ResponseBody ResponseEntity<OperationResponse> save(@RequestBody AccountDTO dto, HttpServletRequest request) {
       String username = getUsername(request);
       OperationResponse response = accountManager.manageOperation(dto, Operation.CREATE, username);
       return new ResponseEntity<>(response, HttpStatus.OK);
   }

    @PutMapping
    public @ResponseBody ResponseEntity<OperationResponse> update(@RequestBody AccountDTO accountDTO, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = accountManager.manageOperation(accountDTO, Operation.MODIFY, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> accountStatusUpdate
            (@RequestBody OperationDTO operationDTO, @PathVariable String accountNumber, HttpServletRequest request) {
        String username = getUsername(request);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(accountNumber);
        OperationResponse response = accountManager.manageOperation(accountDTO, operationDTO.getOperation(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> delete(@PathVariable String accountNumber, HttpServletRequest request) {
        String username = getUsername(request);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(accountNumber);
        OperationResponse response = accountManager.manageOperation(accountDTO, Operation.REMOVE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = accountManager.findAll(new Account(), username);
        if (response.isValid()) {
            response.setDataObject(accountManager.toDTO((List<Account>) response.getObject()));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/usable")
    public @ResponseBody ResponseEntity<OperationResponse> findAllUsableAccounts(HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = accountManager.findAllUsable(new Account(), username);
        if(response.isValid()){
            response.setDataObject(accountManager.toDTO(((List<Account>) response.getObject())));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/find-by-email/{email}")
    public @ResponseBody ResponseEntity<OperationResponse> findByEmail(@PathVariable String email, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = accountManager.findByEmail(email, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findApprovalByName(@PathVariable String accountNumber, HttpServletRequest request) {
        String username = getUsername(request);
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        OperationResponse response = accountManager.findNeedsApproval(account, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval")
    public @ResponseBody ResponseEntity<OperationResponse> findAllApprovalAccounts(HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = accountManager.findAllNeedsApproval(new Account(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse>
    findByAccountNumber(@PathVariable String accountNumber, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = accountManager.findByAccountNumber(accountNumber, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("approve/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> approve(@PathVariable String accountNumber, HttpServletRequest request) {
        String username = getUsername(request);
        AccountDTO account = new AccountDTO();
        account.setAccountNumber(accountNumber);
        OperationResponse response = accountManager.manageOperation(account, Operation.APPROVE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("reject/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> reject(@PathVariable String accountNumber, HttpServletRequest request) {
        String username = getUsername(request);
        AccountDTO account = new AccountDTO();
        account.setAccountNumber(accountNumber);
        OperationResponse response = accountManager.manageOperation(account, Operation.REJECT, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public @ResponseBody ResponseEntity<OperationResponse> findFilteredAccount(@RequestBody AccountFilterDTO accountFilterDTO, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = accountManager.findFilteredAccounts(accountFilterDTO, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

