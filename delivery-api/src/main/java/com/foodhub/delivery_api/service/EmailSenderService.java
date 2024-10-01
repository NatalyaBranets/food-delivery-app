package com.foodhub.delivery_api.service;

import com.foodhub.delivery_api.dto.email.MailInfoDTO;
import com.foodhub.delivery_api.dto.user.UserDTO;

public interface EmailSenderService {
    void sendEmail(MailInfoDTO info);
    String createVerificationMailBody(UserDTO userDTO);
}
