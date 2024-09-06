package com.foodhub.delivery_api.exception.custom_exceptions;

public class PasswordMatchException extends RuntimeException {

    public PasswordMatchException(String msg) {
        super(msg);
    }
}
