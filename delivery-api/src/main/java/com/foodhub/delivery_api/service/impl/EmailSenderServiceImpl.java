package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.dto.email.MailInfoDTO;
import com.foodhub.delivery_api.dto.user.UserDTO;
import com.foodhub.delivery_api.service.EmailSenderService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static com.foodhub.delivery_api.constants.EmailConstants.MAIL_VERIFICATION_TEXT_HTML;

@Slf4j
@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Override
    public void sendEmail(MailInfoDTO info) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

            messageHelper.setFrom(this.username);
            messageHelper.setTo(info.sendTo());
            messageHelper.setText(info.messageText(), true);
            messageHelper.setSubject(info.subject());

            mailSender.send(mimeMessage);
            log.info("Email is sent successfully to {}!", info.sendTo());

        } catch (Exception e) {
            log.error("Email sending is failed! {}", e.getMessage());
        }
    }

    @Override
    public String createVerificationMailBody(UserDTO userDTO) {
        String linkUrlToConfirm = "http://localhost:9000/foodhub/v1/users/verify?code=" + userDTO.verificationCode();
        return String.format(MAIL_VERIFICATION_TEXT_HTML, userDTO.firstName(), linkUrlToConfirm);
    }
}
