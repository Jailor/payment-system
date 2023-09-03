package com.team1.paymentsystem.controllers.history;

import com.team1.paymentsystem.dto.profile.ProfileHistoryDTO;
import com.team1.paymentsystem.entities.Profile;
import com.team1.paymentsystem.entities.history.ProfileHistory;
import com.team1.paymentsystem.mappers.history.ProfileHistoryMapper;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.history.ProfileHistoryService;
import com.team1.paymentsystem.services.entities.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile-history")
public class ProfileHistoryController {
    @Autowired
    ProfileHistoryService profileHistoryService;
    @Autowired
    ProfileHistoryMapper profileHistoryMapper;
    @Autowired
    ProfileService profileService;

    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAll(){
        List<ProfileHistory> profileHistories = profileHistoryService.findAll();
        List<ProfileHistoryDTO> dtos = profileHistories.stream().map(profileHistoryMapper::toDTO).toList();
        return new ResponseEntity<>(new OperationResponse(dtos), HttpStatus.OK);
    }
    @GetMapping("{name}")
    public @ResponseBody ResponseEntity<OperationResponse> findProfileHistory(@PathVariable String name) {
        Profile profile = profileService.findByName(name);
        List<ProfileHistory> profileHistories = profileHistoryService.findByOriginalId(profile.getId());
        List<ProfileHistoryDTO> dtos = profileHistories.stream().map(profileHistoryMapper::toDTO).toList();
        if(!dtos.isEmpty()){
            return new ResponseEntity<>(new OperationResponse(dtos) , HttpStatus.OK);
        }else{
            List<ErrorInfo> errorInfos = List.of(new ErrorInfo(ErrorType.NOT_FOUND_ERROR,"User history not found"));
            OperationResponse operationResponse = new OperationResponse(errorInfos);
            return new ResponseEntity<>(operationResponse,HttpStatus.NOT_FOUND);
        }
    }

}
