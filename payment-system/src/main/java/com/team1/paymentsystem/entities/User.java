package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.AbstractUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@Entity
@Table(name = "user_table")
public class User extends AbstractUser {
}
