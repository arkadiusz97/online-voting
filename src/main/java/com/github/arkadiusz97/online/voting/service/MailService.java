package com.github.arkadiusz97.online.voting.service;

public interface MailService {
    void sendEmail(String recipient, String subject, String content);
}
