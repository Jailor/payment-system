package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.StatusObject;
import com.team1.paymentsystem.entities.common.SystemObject;
import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.converters.CurrencyConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "exchange_rate_table")
public class ExchangeRate extends StatusObject {
    @Convert(converter = CurrencyConverter.class)
    @Column(name = "source_currency", nullable = false)
    private Currency sourceCurrency;
    @Convert(converter = CurrencyConverter.class)
    @Column(name = "destination_currency", nullable = false)
    private Currency destinationCurrency;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "from_date")
    private LocalDateTime fromDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "to_date")
    private LocalDateTime toDate;
    @Column(nullable = false)
    private Double ratio;
}
