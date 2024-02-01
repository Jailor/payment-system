package com.team1.paymentsystem.services;

import com.team1.paymentsystem.dto.login.LoginDTO;
import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.services.entities.UserService;
import com.team1.paymentsystem.states.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService{
    @Autowired
    UserService userService;
    @Autowired
    PasswordAuthentication passwordAuthentication;

    @Override
    public OperationResponse login(String username, String password) {
        OperationResponse response = new OperationResponse();
        User user = userService.findByUsername(username);
        if(user == null) {
            response.addError(new ErrorInfo(ErrorType.AUTHENTICATION_ERROR, "Invalid username or password"));
            return response;
        }
        Status status = user.getStatus();
        boolean okStatus = !status.equals(Status.BLOCKED) && !status.equals(Status.REMOVED)
                && !status.equals(Status.APPROVE) && !status.equals(Status.REPAIR);
        if(!okStatus){
            response.addError(new ErrorInfo(ErrorType.AUTHENTICATION_ERROR, "Your status is "+
                    status.name().toLowerCase()+". Please contact the administrator."));
            return response;
        }
        boolean okPassword = passwordAuthentication.authenticate(password.toCharArray(), user.getPassword());
        if(!okPassword){
            response.addError(new ErrorInfo(ErrorType.AUTHENTICATION_ERROR, "Password is incorrect"));
            return response;
        }
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setProfileName(user.getProfile().getName());
        loginDTO.setPassword(null);
        response.setDataObject(loginDTO);

        return response;
    }
}
