package com.team1.paymentsystem.dto.login;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.ProfileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO extends SystemDTO {
    private String username;
    private String password;
    private String jsonWebToken;
    private String profileName;
}
