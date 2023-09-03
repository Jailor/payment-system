package com.team1.paymentsystem.dto.approval;

import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.ProfileRight;
import com.team1.paymentsystem.states.ProfileType;
import com.team1.paymentsystem.states.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileApproveDTO extends ProfileDTO {
    private ProfileType newProfileType;
    private String newProfileName;
    private List<ProfileRight> newRights;
    private Status newStatus;
    private Operation operation;
}
