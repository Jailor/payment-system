package com.team1.paymentsystem.dto.filter;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.states.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class FilterDTO extends SystemDTO {
    private List<Status> statuses;
}
