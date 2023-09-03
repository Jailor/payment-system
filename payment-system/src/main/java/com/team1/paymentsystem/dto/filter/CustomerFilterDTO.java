package com.team1.paymentsystem.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilterDTO extends FilterDTO{
    private String nameFilter;
    private String addressFilter;
    private String phoneNumberFilter;
    private String emailFilter;
}
