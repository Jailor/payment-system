package com.team1.paymentsystem.managers.response;

import lombok.Getter;

@Getter
public class ErrorInfo {
    private final int errorCode;
    private final String errorMessage;

    public ErrorInfo(ErrorType errorType, String errorMessage) {
        this.errorCode = errorType.ordinal();
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "errorType=" + ErrorType.values()[errorCode].toString() +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
