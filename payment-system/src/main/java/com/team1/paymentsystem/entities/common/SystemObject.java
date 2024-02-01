package com.team1.paymentsystem.entities.common;

import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class SystemObject{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq_gen")
    @SequenceGenerator(name = "entity_seq_gen", sequenceName = "entity_seq", initialValue = 100)
    private long id;
    @Version
    private long version;
}
