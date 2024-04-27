package com.github.arkadiusz97.online.voting.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender emailSender;
    private final String noReplyAddress;
    private final Boolean sendingMailsEnabled;
    private static final Logger logger = LogManager.getLogger(MailServiceImpl.class);

    @Autowired
    public MailServiceImpl(JavaMailSender emailSender,
            @Value("${online-voting.mail.no-reply-address}") String noReplyAddress,
            @Value("${online-voting.mail.sending-mails-enabled}") Boolean sendingMailsEnabled) {
        this.emailSender = emailSender;
        this.noReplyAddress = noReplyAddress;
        this.sendingMailsEnabled = sendingMailsEnabled;
    }

    @Override
    public void sendEmail(String recipient, String subject, String content) {
        if(sendingMailsEnabled) {
            logger.debug("Sending email to '{}' with subject '{}' and content '{}'", recipient, subject, content);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom(noReplyAddress);
            try {
                emailSender.send(message);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.debug(
                "Sending emails is disabled. Sended email would be: " +
                "to {} with subject {} and content {}", recipient, subject, content
            );
        }
    }

}
