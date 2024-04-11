package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.repository.RoleRepository;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import com.github.arkadiusz97.online.voting.repository.UserRoleRepository;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordGeneratorService passwordGeneratorService;

    @BeforeEach
    public void setup() {
        userServiceImpl = new UserServiceImpl(userRepository, userRoleRepository, roleRepository, mailService,
            passwordEncoder, passwordGeneratorService);
    }

    @Test
    public void whenShowMany_thenGetUsers() {
        LinkedList<User> users = SampleDomains.getSampleUsers();
        PageRequest pageRequest = PageRequest.of(1, 2);
        List<User> usersFromPage = Arrays.asList(users.get(3), users.get(4));
        Page<User> userPage = new PageImpl<>(usersFromPage, pageRequest, 2);
        Mockito.when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        List<UserDTO> result = userServiceImpl.showMany(1, 2);
        assertThat("some-mail4@domain.eu").isEqualTo(result.get(0).email());
        assertThat(2).isEqualTo(result.size());
    }

    @Test
    public void whenRegisterNewUser_thenGetThisUser() {
        Mockito.when(roleRepository.findByName("ROLE_USER")).thenReturn(new Role("ROLE_USER"));
        String userMail = "some-user@domain.com";
        String message = userServiceImpl.registerNewUser(userMail);
        assertThat(message).isEqualTo("Registered a new user with " + userMail + " email");
    }
}
