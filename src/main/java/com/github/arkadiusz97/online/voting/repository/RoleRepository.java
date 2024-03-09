package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
