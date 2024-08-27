package com.foodhub.delivery_api.exception;

public class PasswordMatchException extends RuntimeException {

    public PasswordMatchException(String msg) {
        super(msg);
    }
}
