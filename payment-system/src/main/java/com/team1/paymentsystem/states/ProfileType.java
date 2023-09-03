package com.team1.paymentsystem.states;

public class ProfileType extends AbstractState{
    public ProfileType(String name) {
        super(name);
    }
    public static final ProfileType ADMINISTRATOR = new ProfileType("ADMINISTRATOR");
    public static final ProfileType EMPLOYEE = new ProfileType("EMPLOYEE");
    public static final ProfileType CUSTOMER = new ProfileType("CUSTOMER");
}
