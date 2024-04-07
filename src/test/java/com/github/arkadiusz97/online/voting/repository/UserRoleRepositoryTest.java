package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserRole;
import com.github.arkadiusz97.online.voting.domain.UserRoleKey;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRoleRepositoryTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void whenDeleteAllByUser_ThenDeleteWantedUserRoles() {
        LinkedList<User> usersToSave = SampleDomains.getSampleUsers();
        usersToSave.forEach(user -> {
            userRepository.save(user);
        });
        User user1 = userRepository.findAll().get(0);
        User user2 = userRepository.findAll().get(1);
        User user3 = userRepository.findAll().get(2);

        Role role1 = roleRepository.save(new Role("ROLE_USER"));
        Role role2 = roleRepository.save(new Role("ROLE_ADMIN"));

        UserRoleKey urk1 = new UserRoleKey(user1.getId(), role1.getId());
        userRoleRepository.save(new UserRole(urk1, user1, role1));
        UserRoleKey urk2 = new UserRoleKey(user2.getId(), role2.getId());
        userRoleRepository.save(new UserRole(urk2, user2, role2));

        assertThat(2).isEqualTo(userRoleRepository.findAll().size());
        userRoleRepository.deleteAllByUser(user1);
        assertThat(1).isEqualTo(userRoleRepository.findAll().size());
        userRoleRepository.deleteAllByUser(user3);
        assertThat(1).isEqualTo(userRoleRepository.findAll().size());
        userRoleRepository.deleteAllByUser(user2);
        assertThat(0).isEqualTo(userRoleRepository.findAll().size());
    }

}
