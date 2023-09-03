package com.team1.paymentsystem.dto.login;

import com.team1.paymentsystem.dto.common.SystemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO extends SystemDTO {
    private String username;
    private String oldPassword;
    private String newPassword;
    private String jsonWebToken;
}
