package com.mybatuaa.exception;

public class EmptyTransactionListException extends RuntimeException {
    public EmptyTransactionListException(String message) {
        super(message);
    }
}

