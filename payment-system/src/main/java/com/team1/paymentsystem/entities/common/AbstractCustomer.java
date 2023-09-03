package com.team1.paymentsystem.entities.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractCustomer extends StatusObject{
    @Column(name = "name", nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(nullable = false)
    private String email;
    private String country;
    private String state;
    private String city;
    private String defaultAccountNumber;
}
