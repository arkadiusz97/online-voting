package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.repository.RoleRepository;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import com.github.arkadiusz97.online.voting.repository.UserRoleRepository;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
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

    @Test
    public void showManyTest() {
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
    public void registerNewUserTest() {
        Mockito.when(roleRepository.findByName("ROLE_USER")).thenReturn(new Role("ROLE_USER"));
        String userMail = "some-user@domain.com";
        String message = userServiceImpl.registerNewUser(userMail);
        assertThat(message).isEqualTo("Registered a new user with " + userMail + " email");
    }

    @Test
    public void registerNewAdminTest() {
        Mockito.when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(new Role("ROLE_ADMIN"));
        String userMail = "some-user@domain.com";
        String message = userServiceImpl.registerNewAdmin(userMail);
        assertThat(message).isEqualTo("Registered a new user with " + userMail + " email");
    }

    @Test
    public void getByIdTest() {
        LinkedList<User> users = SampleDomains.getSampleUsers();
        User user = users.get(0);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDTO result = userServiceImpl.getById(1L);
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.created()).isEqualTo(user.getCreated());
    }

    @Test
    public void getByEmailTest() {
        LinkedList<User> users = SampleDomains.getSampleUsers();
        User user = users.get(0);
        Mockito.when(userRepository.findByEmail("some-mail1@domain.eu")).thenReturn(user);
        UserDTO result = userServiceImpl.getByEmail("some-mail1@domain.eu");
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.created()).isEqualTo(user.getCreated());
    }

    @Test
    public void deleteUserTest() {
        Long userId = 1L;
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        LinkedList<User> users = SampleDomains.getSampleUsers();
        User user = users.get(0);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThatNoException().isThrownBy(() -> userServiceImpl.delete(userId));
    }

    @Test
    public void getCurrentUserTest() {
        LinkedList<User> users = SampleDomains.getSampleUsers();
        User user = users.get(0);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(mock(Authentication.class));
        Mockito.when(securityContext.getAuthentication().getName())
                .thenReturn(user.getEmail());
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        User currentUser = userServiceImpl.getCurrentUser();
        assertThat(currentUser.getEmail()).isEqualTo(currentUser.getEmail());
    }

    @Test
    public void changeCurrentUserPasswordTest() {
        LinkedList<User> users = SampleDomains.getSampleUsers();
        User user = users.get(0);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(mock(Authentication.class));
        Mockito.when(securityContext.getAuthentication().getName())
                .thenReturn(user.getEmail());
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        String newPassword = "aa00AA--";
        userServiceImpl.changeCurrentUserPassword(newPassword);
        verify(passwordEncoder, times(1)).encode(newPassword);
    }

    @Test
    public void getDTOTest() {
        LinkedList<User> users = SampleDomains.getSampleUsers();
        User user = users.get(0);
        UserDTO dto = userServiceImpl.getDTO(user);
        assertThat(user.getEmail()).isEqualTo(dto.email());
        assertThat(user.getId()).isEqualTo(dto.id());
        assertThat(user.getCreated()).isEqualTo(dto.created());
    }

}
