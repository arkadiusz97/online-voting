package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.dto.responsebody.AboutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AboutServiceImplTest {

    private AboutServiceImpl aboutServiceImpl;

    @BeforeEach
    public void init() {
        aboutServiceImpl = new AboutServiceImpl("123");
    }

    @Test
    public void getAboutTest() {
        AboutDTO dto = aboutServiceImpl.getAbout();
        assertThat(dto.version()).isEqualTo("123");
        assertThat(dto.version()).isNotNull();
    }
}
