package com.team1.paymentsystem.dto.approval;

import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserApproveDTO extends UserDTO {
    private String newUsername;
    private String newPassword;
    private String newFullName;
    private String newEmail;
    private String newAddress;
    private Status newStatus;
    private String newProfileName;
    private Operation operation;
}
