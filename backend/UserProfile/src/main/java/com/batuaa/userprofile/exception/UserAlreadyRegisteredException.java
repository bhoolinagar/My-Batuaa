package com.batuaa.userprofile.exception;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}