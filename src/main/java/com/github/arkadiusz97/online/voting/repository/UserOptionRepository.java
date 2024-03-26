package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.UserOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOptionRepository extends JpaRepository<UserOption, Long> {
}
