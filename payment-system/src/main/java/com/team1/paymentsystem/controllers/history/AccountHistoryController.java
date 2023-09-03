package com.team1.paymentsystem.controllers.history;

import com.team1.paymentsystem.dto.account.AccountHistoryDTO;
import com.team1.paymentsystem.mappers.history.AccountHistoryMapper;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.history.AccountHistory;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.history.AccountHistoryService;
import com.team1.paymentsystem.services.entities.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/account-history")
public class AccountHistoryController {
    @Autowired
    AccountHistoryService accountHistoryService;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountHistoryMapper accountHistoryMapper;
    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(){
        List<AccountHistory> accountHistories = accountHistoryService.findAll();
        List<AccountHistoryDTO> dtos = accountHistories.stream().map(accountHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }
    @GetMapping("{accountNumber}")
    public @ResponseBody ResponseEntity<OperationResponse> findAccountHistory(@PathVariable String accountNumber){
        Account account = accountService.findByAccountNumber(accountNumber);
        List<AccountHistory> accountHistories = accountHistoryService.findByOriginalId(account.getId());
        List<AccountHistoryDTO> dtos = accountHistories.stream().map(accountHistoryMapper::toDTO).toList();
        if(!dtos.isEmpty()){
            return new ResponseEntity<>(new OperationResponse(dtos),HttpStatus.OK);
        }else{
            List<ErrorInfo> errorInfos = List.of(new ErrorInfo(ErrorType.NOT_FOUND_ERROR,"User history not found"));
            OperationResponse operationResponse = new OperationResponse(errorInfos);
            return new ResponseEntity<>(operationResponse,HttpStatus.NOT_FOUND);
        }
    }
}
