package com.team1.paymentsystem.entities;

import com.team1.paymentsystem.entities.common.AbstractProfile;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "profile_table")
public class Profile extends AbstractProfile {
}
