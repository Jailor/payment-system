package com.team1.paymentsystem.entities.common;

import com.team1.paymentsystem.states.ProfileType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractProfile extends StatusObject {
    @Enumerated(EnumType.STRING)
    @Column(name="profile_type", nullable = false)
    private ProfileType profileType;
    @Column(nullable = false)
    private String name;
    @Column(length = 2048, nullable = false)
    private String rights;
}
