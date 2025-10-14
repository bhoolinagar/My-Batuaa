package com.myBatuaa.exception;

public class UnableToAddMoneyException extends RuntimeException {
    public UnableToAddMoneyException(String message, Exception e) {
        super(message);
    }
}
