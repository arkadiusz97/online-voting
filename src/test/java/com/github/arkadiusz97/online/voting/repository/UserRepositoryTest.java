package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindAll_ThenReturnWantedUsers() {
        LinkedList<User> usersToSave = SampleDomains.getSampleUsers();
        usersToSave.forEach(user -> {
            userRepository.save(user);
        });
        Pageable pageable = PageRequest.of(1, 2);
        List<User> users = userRepository.findAll(pageable).stream().collect(Collectors.toList());

        assertThat(5).isEqualTo(userRepository.findAll().size());
        assertThat(2).isEqualTo(users.size());
        assertThat("some-mail3@domain.eu").isEqualTo(users.get(0).getEmail());
        assertThat("111").isEqualTo(users.get(1).getPassword());
    }
}
