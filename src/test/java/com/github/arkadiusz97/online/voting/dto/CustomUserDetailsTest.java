package com.github.arkadiusz97.online.voting.dto;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsTest {

    private CustomUserDetails customUserDetails;
    private User user;

    @BeforeEach
    public void init() {
        user = SampleDomains.getSampleUser();
        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    public void gettersTest() {
        assertThat(user.getEmail()).isEqualTo(customUserDetails.getUsername());
        assertThat(user.getPassword()).isEqualTo(customUserDetails.getPassword());
        assertThat(Optional.empty()).isEqualTo(Optional.ofNullable(customUserDetails.getAuthorities()));
        assertThat(true).isEqualTo(customUserDetails.isAccountNonExpired());
        assertThat(true).isEqualTo(customUserDetails.isAccountNonLocked());
        assertThat(true).isEqualTo(customUserDetails.isCredentialsNonExpired());
        assertThat(true).isEqualTo(customUserDetails.isEnabled());
        assertThat(user).isEqualTo(customUserDetails.getUser());
    }

}
