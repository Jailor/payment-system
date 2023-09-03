package com.team1.paymentsystem.dto.filter;

import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFilterDTO {
    List<PaymentStatus> statuses;
    List<Currency> currencies;
    private String systemReferenceFilter;
}
