package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.dto.balance.BalanceEntryDTO;
import com.team1.paymentsystem.dto.balance.BalanceRevealDTO;
import com.team1.paymentsystem.managers.common.AuthorizationManager;
import com.team1.paymentsystem.mappers.entity.BalanceMapper;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Balance;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.AccountService;
import com.team1.paymentsystem.services.entities.BalanceService;
import com.team1.paymentsystem.states.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.team1.paymentsystem.controllers.CommonUtils.getUsername;

@RestController
@RequestMapping("api/balance")
public class BalanceController {
    @Autowired
    BalanceService balanceService;
    @Autowired
    BalanceMapper balanceMapper;
    @Autowired
    AccountService accountService;
    @Autowired
    AuthorizationManager authorizationManager;
    @PostMapping
    public @ResponseBody ResponseEntity<OperationResponse> save(@RequestBody BalanceEntryDTO balanceEntryDto, HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = authorizationManager.authorizeProfile(new Account(), username, Operation.CREATE);
        if(response.isValid()){
            Account account = accountService.findByAccountNumber(balanceEntryDto.getAccountNumber());
            Balance balance = balanceService.findLastBalanceByAccount(account);
            balanceService.save(balance);
            BalanceRevealDTO balanceRevealDTO = balanceMapper.toDTO(balance);
            response.setDataObject(balanceRevealDTO);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = authorizationManager.authorizeProfile(new Account(), username, Operation.LIST);
        if(response.isValid()){
            List<Balance> balances = balanceService.findAll();
            List<BalanceRevealDTO> dtos = balances.stream().map(balanceMapper::toDTO).toList();
            response.setDataObject(dtos);
        }

        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/all/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findAllBalancesAccount(@PathVariable String accountNumber, HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = authorizationManager.authorizeProfile(new Account(), username, Operation.LIST);
        if(response.isValid()){
            Account account = accountService.findByAccountNumber(accountNumber);
            List<Balance> balances = balanceService.findByAccount(account);
            List<BalanceRevealDTO> dtos = balances.stream().map(balanceMapper::toDTO).toList();
            response.setDataObject(dtos);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/last/{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findLastBalanceAccount(@PathVariable String accountNumber, HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = authorizationManager.authorizeProfile(new Account(), username, Operation.LIST);
        if(response.isValid()) {
            Account account = accountService.findByAccountNumber(accountNumber);
            Balance balance = balanceService.findLastBalanceByAccount(account);
            BalanceRevealDTO balanceRevealDTO = balanceMapper.toDTO(balance);
            response.setDataObject(balanceRevealDTO);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
