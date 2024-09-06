package com.foodhub.delivery_api.exception;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String message;
    private List<FieldViolation> violations;
    private String description;

    public ErrorMessage(int statusCode, Date timestamp, String message, List<FieldViolation> violations, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.violations = violations;
        this.description = description;
    }

    public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }
}
