package com.foodhub.delivery_api.exception.custom_exceptions;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
