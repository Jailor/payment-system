package com.team1.paymentsystem.controllers.history;

import com.team1.paymentsystem.mappers.history.UserHistoryMapper;
import com.team1.paymentsystem.dto.user.UserHistoryDTO;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.entities.history.UserHistory;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.history.UserHistoryService;
import com.team1.paymentsystem.services.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-history")
public class UserHistoryController {
    @Autowired
    protected UserHistoryService userHistoryService;
    @Autowired
    UserHistoryMapper userHistoryMapper;
    @Autowired
    protected UserService userService;
    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(){
        List<UserHistory> userHistories = userHistoryService.findAll();
        List<UserHistoryDTO> dtos = userHistories.stream().map(userHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos),HttpStatus.OK);
    }
    @GetMapping("{username}")
    public @ResponseBody ResponseEntity<OperationResponse> findUserHistory(@PathVariable String username){
        User user = userService.findByUsername(username);
        List<UserHistory> history = userHistoryService.findByOriginalId(user.getId());
        List<UserHistoryDTO> dtos = history.stream().map(userHistoryMapper::toDTO).toList();
        if(!dtos.isEmpty()){
            return new ResponseEntity<>(new OperationResponse(dtos) , HttpStatus.OK);
        }else{
            List<ErrorInfo> errorInfos = List.of(new ErrorInfo(ErrorType.NOT_FOUND_ERROR,"User history not found"));
            OperationResponse operationResponse = new OperationResponse(errorInfos);
            return new ResponseEntity<>(operationResponse,HttpStatus.NOT_FOUND);
        }
    }
}
