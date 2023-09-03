package com.team1.paymentsystem.dto.approval;

import com.team1.paymentsystem.dto.customer.CustomerDTO;
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
public class CustomerApproveDTO extends CustomerDTO {
    private String newName;
    private String newAddress;
    private String newPhoneNumber;
    private String newEmail;
    private String newCity;
    private String newState;
    private String newCountry;
    private Operation operation;
    private Status newStatus;
    private String newDefaultAccountNumber;
}
