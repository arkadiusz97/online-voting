package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAll(Pageable pageable);
    User findByEmail(String email);
}
