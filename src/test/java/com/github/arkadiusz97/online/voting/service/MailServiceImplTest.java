package com.github.arkadiusz97.online.voting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MailServiceImplTest {

    private MailServiceImpl mailServiceImpl;

    @Mock
    private JavaMailSender emailSender;

    @Test
    public void sendEmailTestWhenEnabled() {
        mailServiceImpl = new MailServiceImpl(emailSender, "no-reply@domain.com", true);
        mailServiceImpl.sendEmail("recipient@domain.com", "subject", "content");
        verify(emailSender, times(1)).send((SimpleMailMessage) any());
    }

    @Test
    public void dontSendEmailTestWhenDisabled() {
        mailServiceImpl = new MailServiceImpl(emailSender, "no-reply@domain.com", false);
        mailServiceImpl.sendEmail("recipient@domain.com", "subject", "content");
        verify(emailSender, times(0)).send((SimpleMailMessage) any());
    }

    @Test
    public void handleThrowingExceptionByEmailSender() {
        Mockito.doThrow(new RuntimeException()).when(emailSender).send((SimpleMailMessage) any());
        mailServiceImpl = new MailServiceImpl(emailSender, "no-reply@domain.com", true);
        mailServiceImpl.sendEmail("recipient@domain.com", "subject", "content");
        assertThatException().isThrownBy(() -> emailSender.send((SimpleMailMessage) any()));
    }

}
