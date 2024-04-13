package com.github.arkadiusz97.online.voting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PasswordGeneratorServiceImplTest {

    @InjectMocks
    private PasswordGeneratorServiceImpl passwordGeneratorServiceImpl;

    @Test
    public void generatePasswordTest() {
        String randomPassword = passwordGeneratorServiceImpl.generatePassword();
        assertThat(randomPassword.length()).isEqualTo(8);
    }

}
