package com.myBatuaa.exception;

public class BankAccountAlreadyLinkedException extends RuntimeException {
    public BankAccountAlreadyLinkedException(String message) {
        super(message);
    }
}