package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.AbstractPayment;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "payment_table")
public class Payment extends AbstractPayment {
}
