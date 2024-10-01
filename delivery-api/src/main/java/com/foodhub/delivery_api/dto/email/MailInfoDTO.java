package com.foodhub.delivery_api.dto.email;

public record MailInfoDTO (
    String sendTo,
    String subject,
    String messageText
) {
}
