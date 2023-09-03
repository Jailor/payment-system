package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.AbstractCustomer;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "customer_table")
public class Customer extends AbstractCustomer {

}
