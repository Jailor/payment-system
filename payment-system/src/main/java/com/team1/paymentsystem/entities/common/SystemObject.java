package com.team1.paymentsystem.entities.common;

import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class SystemObject{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Version
    private long version;
}
