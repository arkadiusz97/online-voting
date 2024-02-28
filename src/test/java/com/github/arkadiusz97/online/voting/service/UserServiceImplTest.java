package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    /*@TestConfiguration
    static class UserServiceImplTestContextConfiguration {
        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }
    }*/
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        Date now = new Date();
        User user1 = new User(1L, "some-mail1@domain.eu", "abc123", now, false);
        User user2 = new User(2L, "some-mail2@domain.eu", "abc", now, true);
        User user3 = new User(3L, "some-mail3@domain.eu", "abc999", now, false);
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2), pageRequest, 2);
        Mockito.when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        userServiceImpl = new UserServiceImpl(userRepository);
    }

    @Test
    public void whenShowMany_thenGetUsers() {
        List<UserDTO> users = userServiceImpl.showMany(0, 2);
        assertThat("some-mail1@domain.eu").isEqualTo(users.get(0).email());
        assertThat(true).isEqualTo(users.get(1).isAdmin());
        assertThat(2).isEqualTo(users.size());
    }
}
