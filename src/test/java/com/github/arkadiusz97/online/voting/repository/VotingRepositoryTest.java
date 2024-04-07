package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class VotingRepositoryTest {

    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void whenFindAll_ThenReturnAllVotings() {
        User user = userRepository.save(SampleDomains.getSampleUser());
        LinkedList<Voting> votingsToSave = SampleDomains.getSampleVotings(user);
        votingsToSave.forEach(voting -> {
            votingRepository.save(voting);
        });
        assertThat(3).isEqualTo(votingRepository.findAll().size());
    }
}
