package com.team1.paymentsystem.dto.filter;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.ProfileRight;
import com.team1.paymentsystem.states.ProfileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileFilterDTO extends FilterDTO {
    private List<ProfileType> types;
    private String nameFilter;
    private List<ProfileRight> rights;
}
