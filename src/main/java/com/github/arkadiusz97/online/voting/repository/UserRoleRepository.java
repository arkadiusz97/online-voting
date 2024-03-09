package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
