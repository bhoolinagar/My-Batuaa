package com.mybatuaa.exception;

public class UnableToAddMoneyException extends RuntimeException {
    public UnableToAddMoneyException(String message, Exception e) {
        super(message);
    }
}
