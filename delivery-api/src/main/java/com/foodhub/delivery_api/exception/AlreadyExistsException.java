package com.foodhub.delivery_api.exception;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
