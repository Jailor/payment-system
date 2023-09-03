package com.team1.paymentsystem.dto.user;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractUserDTO extends SystemDTO {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String address;
    private Status status;
    private String profileName;
    private Boolean needsApproval;
}
