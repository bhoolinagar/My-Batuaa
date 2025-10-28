package com.batuaa.transactionservice.exception;

public class InvalidEmailAddressException extends Exception {
    public InvalidEmailAddressException(String message) {
        super(message);
    }
}
