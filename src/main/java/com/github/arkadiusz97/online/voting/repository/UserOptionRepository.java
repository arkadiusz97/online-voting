package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOptionRepository extends JpaRepository<UserOption, Long> {
    List<UserOption> findAllByUser(User user);
}
