package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.Voting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingRepository extends JpaRepository<Voting, Long> {
}
