package com.team1.paymentsystem.states;

public class Status extends AbstractState {
    public Status(String name) {
        super(name);
    }
    public static final Status ACTIVE = new Status("ACTIVE");
    public static final Status REPAIR = new Status("REPAIR");
    public static final Status REMOVED = new Status("REMOVED");
    public static final Status BLOCKED = new Status("BLOCKED");
    public static final Status APPROVE = new Status("APPROVE");
}
