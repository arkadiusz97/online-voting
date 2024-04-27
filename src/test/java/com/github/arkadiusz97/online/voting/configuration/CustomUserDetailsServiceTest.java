package com.github.arkadiusz97.online.voting.configuration;

import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserRole;
import com.github.arkadiusz97.online.voting.domain.UserRoleKey;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import com.github.arkadiusz97.online.voting.repository.UserRoleRepository;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Test
    public void loadUserByUsernameTest() {
        String email = "some-email@domain.com";
        User user = SampleDomains.getSampleUser();
        UserRole userRole = new UserRole(new UserRoleKey(1L, 1L), user, new Role("ROLE_USER"));
        user.setUsersRoles(Set.of(userRole));
        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        assertThat(user.getEmail()).isEqualTo(userDetails.getUsername());
        assertThat(user.getPassword()).isEqualTo(userDetails.getPassword());
        assertThat(false).isEqualTo(userDetails.getAuthorities().isEmpty());
        assertThat(true).isEqualTo(userDetails.isAccountNonExpired());
        assertThat(true).isEqualTo(userDetails.isAccountNonLocked());
        assertThat(true).isEqualTo(userDetails.isCredentialsNonExpired());
        assertThat(true).isEqualTo(userDetails.isEnabled());
    }

    @Test
    public void loadUserByUsernameWhenDoesntExistTest() {
        String email = "some-email@domain.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);
        assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() ->
                customUserDetailsService.loadUserByUsername(email)
        );
    }

}
