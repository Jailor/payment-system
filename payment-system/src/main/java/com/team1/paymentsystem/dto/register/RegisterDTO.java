package com.team1.paymentsystem.dto.register;

import com.team1.paymentsystem.dto.common.SystemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO extends SystemDTO {
    private String username;
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;
    private String password;
    private String state;
    private String country;
    private String city;
}
