package com.team1.paymentsystem.states;

public class AccountStatus extends AbstractState{

    public AccountStatus(String name) {
        super(name);
    }
    public static final AccountStatus OPEN = new AccountStatus("OPEN");
    public static final AccountStatus BLOCKED_CREDIT = new AccountStatus("BLOCKED_CREDIT");
    public static final AccountStatus BLOCKED_DEBIT = new AccountStatus("BLOCKED_DEBIT");
    public static final AccountStatus BLOCKED = new AccountStatus("BLOCKED");
    public static final AccountStatus CLOSED = new AccountStatus("CLOSED");
}
