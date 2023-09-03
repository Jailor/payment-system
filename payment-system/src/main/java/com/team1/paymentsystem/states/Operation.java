package com.team1.paymentsystem.states;

public class Operation extends AbstractState {
    public Operation(String name) {
        super(name);
    }
    public static final Operation LIST = new Operation("LIST");
    public static final Operation CREATE = new Operation("CREATE");
    public static final Operation REPAIR = new Operation("REPAIR");
    public static final Operation MODIFY = new Operation("MODIFY");
    public static final Operation BLOCK = new Operation("BLOCK");
    public static final Operation BLOCK_CREDIT = new Operation("BLOCK_CREDIT");
    public static final Operation BLOCK_DEBIT = new Operation("BLOCK_DEBIT");
    public static final Operation UNBLOCK = new Operation("UNBLOCK");
    public static final Operation UNBLOCK_CREDIT = new Operation("UNBLOCK_CREDIT");
    public static final Operation UNBLOCK_DEBIT = new Operation("UNBLOCK_DEBIT");
    public static final Operation CLOSE = new Operation("CLOSE");
    public static final Operation REMOVE = new Operation("REMOVE");
    public static final Operation APPROVE = new Operation("APPROVE");
    public static final Operation REJECT = new Operation("REJECT");
    public static final Operation VERIFY = new Operation("VERIFY");
    public static final Operation AUTHORIZE = new Operation("AUTHORIZE");
    public static final Operation CANCEL = new Operation("CANCEL");
    public static final Operation UNBLOCK_FRAUD = new Operation("UNBLOCK_FRAUD");
    public static final Operation CREATE_MOBILE = new Operation("CREATE_MOBILE");

}
