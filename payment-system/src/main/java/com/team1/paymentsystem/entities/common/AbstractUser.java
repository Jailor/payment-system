package com.team1.paymentsystem.entities.common;

import com.team1.paymentsystem.entities.Profile;
import jakarta.persistence.*;
import lombok.*;

@Data
@MappedSuperclass
public abstract class AbstractUser extends StatusObject {
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(name = "full_name")
    private String fullName;
    private String email;
    private String address;
    @ManyToOne(targetEntity = Profile.class)
    private Profile profile;
}
