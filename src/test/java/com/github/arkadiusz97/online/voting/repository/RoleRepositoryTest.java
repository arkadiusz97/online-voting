package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void whenFindByName_ThenReturnWantedRoles() {
        roleRepository.save(new Role("ROLE_USER"));
        roleRepository.save(new Role("ROLE_ADMIN"));

        assertThat(2).isEqualTo(roleRepository.findAll().size());
        assertThat("ROLE_USER").isEqualTo(roleRepository.findByName("ROLE_USER").getName());
        assertThat("ROLE_ADMIN").isEqualTo(roleRepository.findByName("ROLE_ADMIN").getName());
    }

}
