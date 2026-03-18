package com.nbro.service;

import com.nbro.domain.common.AppEnums;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender javaMailSender;

    public void sendStatusChangeEmail(String toAddress,
                                      String releaseKey,
                                      AppEnums.ReleaseStatus fromStatus,
                                      AppEnums.ReleaseStatus toStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toAddress);
        message.setSubject("Release " + releaseKey + " status changed");
        message.setText("Release " + releaseKey + " has moved from " + fromStatus + " to " + toStatus);
        try {
            javaMailSender.send(message);
            logger.info("Status change email sent successfully to: {}", toAddress);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toAddress, e.getMessage());
        }
    }

}
