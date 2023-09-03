package com.team1.paymentsystem.dto.customer;

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
public abstract class AbstractCustomerDTO extends SystemDTO {
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String country;
    private String state;
    private String city;
    private Status status;
    private String defaultAccountNumber;
    private Boolean needsApproval;
}
