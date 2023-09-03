package com.team1.paymentsystem.dto.profile;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.ProfileRight;
import com.team1.paymentsystem.states.ProfileType;
import com.team1.paymentsystem.states.Status;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractProfileDTO extends SystemDTO {
    private ProfileType profileType;
    private String name;
    private List<ProfileRight> rights;
    private Status status;
    private Boolean needsApproval;
}
