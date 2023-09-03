package com.team1.paymentsystem.states;

public class PaymentStatus extends AbstractState{
    public PaymentStatus(String name) {
        super(name);
    }
    public static final PaymentStatus REPAIR = new PaymentStatus("REPAIR");
    public static final PaymentStatus VERIFY = new PaymentStatus("VERIFY");
    public static final PaymentStatus APPROVE = new PaymentStatus("APPROVE");
    public static final PaymentStatus AUTHORIZE = new PaymentStatus("AUTHORIZE");
    public static final PaymentStatus CANCELLED = new PaymentStatus("CANCELLED");
    public static final PaymentStatus COMPLETED = new PaymentStatus("COMPLETED");
    public static final PaymentStatus BLOCKED_BY_FRAUD = new PaymentStatus("BLOCKED_BY_FRAUD");
}
