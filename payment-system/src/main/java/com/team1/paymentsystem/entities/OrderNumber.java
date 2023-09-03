package com.team1.paymentsystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "order_number")
public class OrderNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Temporal(TemporalType.DATE)
    private LocalDate date;
    private Long number;
}
