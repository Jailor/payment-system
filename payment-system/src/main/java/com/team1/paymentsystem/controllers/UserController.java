package com.team1.paymentsystem.controllers;

import com.team1.paymentsystem.dto.OperationDTO;
import com.team1.paymentsystem.dto.filter.UserFilterDTO;
import com.team1.paymentsystem.dto.login.ChangePasswordDTO;
import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.managers.UserManager;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.states.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.team1.paymentsystem.controllers.CommonUtils.getUsername;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserManager userManager;

    @PostMapping
    public @ResponseBody ResponseEntity<OperationResponse> save (@RequestBody UserDTO userDTO, HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = userManager.manageOperation(userDTO, Operation.CREATE, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping
    public @ResponseBody ResponseEntity<OperationResponse> update(@RequestBody UserDTO userDTO, HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response =  userManager.manageOperation(userDTO, Operation.MODIFY, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{username}")
    public @ResponseBody ResponseEntity<OperationResponse> userStatusUpdate(
            @RequestBody OperationDTO operationDTO, @PathVariable String username, HttpServletRequest request) {
        String username1 = getUsername(request);
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        OperationResponse response = userManager.manageOperation(userDTO, operationDTO.getOperation(), username1);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping
    public @ResponseBody ResponseEntity<OperationResponse> findAllUsers(HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = userManager.findAll(new User(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/usable")
    public @ResponseBody ResponseEntity<OperationResponse> findAllUsable(HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = userManager.findAllUsable(new User(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval")
    public @ResponseBody ResponseEntity<OperationResponse> findAllApprovalUsers(HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = userManager.findAllNeedsApproval(new User(), username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requires-approval/{opUser}")
    public @ResponseBody ResponseEntity<OperationResponse> findApprovalByUsername(@PathVariable String opUser, HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = userManager.findNeedsApproval(opUser, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/{opUser}")
    public @ResponseBody ResponseEntity<OperationResponse> remove(@PathVariable String opUser, HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = userManager.remove(opUser, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/approve/{opUser}")
    public @ResponseBody ResponseEntity<OperationResponse> approve(@PathVariable String opUser, HttpServletRequest request){
        String username = getUsername(request);
        OperationResponse response = userManager.approve(opUser, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reject/{opUser}")
    public @ResponseBody ResponseEntity<OperationResponse> reject(@PathVariable String opUser, HttpServletRequest request) {
        String username = getUsername(request);
        OperationResponse response = userManager.reject(opUser, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public @ResponseBody ResponseEntity<OperationResponse> findByUsername(@PathVariable String username, HttpServletRequest request){
        String username2 = getUsername(request);
        User user = new User();
        user.setUsername(username);
        OperationResponse response = userManager.findByDiscriminant(user, username2);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/filter")
    public @ResponseBody ResponseEntity<OperationResponse> findFilteredUsers(@RequestBody UserFilterDTO userFilterDTO){
        OperationResponse response = userManager.filter(userFilterDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public @ResponseBody ResponseEntity<OperationResponse> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){
        OperationResponse response = userManager.changePassword(changePasswordDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
