package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
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

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        LinkedList<User> usersToSave = SampleDomains.getSampleUsers();
        usersToSave.forEach(user -> {
            userRepository.save(user);
        });
        PageRequest pageRequest = PageRequest.of(1, 2);
        List<User> usersFromPage = Arrays.asList(usersToSave.get(3), usersToSave.get(4));
        Page<User> userPage = new PageImpl<>(usersFromPage, pageRequest, 2);
        Mockito.when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        userServiceImpl = new UserServiceImpl(userRepository);
    }

    @Test
    public void whenShowMany_thenGetUsers() {
        List<UserDTO> users = userServiceImpl.showMany(1, 2);
        assertThat("some-mail4@domain.eu").isEqualTo(users.get(0).email());
        assertThat(false).isEqualTo(users.get(1).isAdmin());
        assertThat(2).isEqualTo(users.size());
    }
}
