package com.batuaa.transactionservice.exception;

public class InvalidWalletTransactionException extends RuntimeException {
    public InvalidWalletTransactionException(String message) {
        super(message);
    }
}
