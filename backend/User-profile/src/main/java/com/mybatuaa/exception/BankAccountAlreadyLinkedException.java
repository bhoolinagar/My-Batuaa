package com.mybatuaa.exception;

public class BankAccountAlreadyLinkedException extends RuntimeException {
    public BankAccountAlreadyLinkedException(String message) {
        super(message);
    }
}