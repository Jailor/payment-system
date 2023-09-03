package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.AbstractAccount;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name="account_table")
public class Account extends AbstractAccount {
}
