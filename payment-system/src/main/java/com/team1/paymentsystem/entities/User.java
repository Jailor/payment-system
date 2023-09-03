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
//    @Column(name = "is_using_2fa")
//    private Boolean isUsing2FA;
//    private String secret;
//    public User(){
//        super();
//        SecureRandom secureRandom = new SecureRandom();
//        int bytes = (int) (System.currentTimeMillis() % 100);
//        secureRandom.setSeed(secureRandom.generateSeed(bytes));
//        this.secret = String.valueOf(secureRandom.nextInt(1000000));
//   }
}
