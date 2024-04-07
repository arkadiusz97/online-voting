package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.Option;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OptionRepositoryTest {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VotingRepository votingRepository;

    @Test
    public void whenFindAllByVoting_ThenReturnWantedOptions() {
        User user = userRepository.save(SampleDomains.getSampleUser());
        LinkedList<Voting> votingsToSave = SampleDomains.getSampleVotings(user);
        votingsToSave.forEach(voting -> {
            votingRepository.save(voting);
        });

        Voting voting1 = votingRepository.findAll().get(0);
        Voting voting2 = votingRepository.findAll().get(1);
        Voting voting3 = votingRepository.findAll().get(2);
        LinkedList<Option> optionsToSaveFor1Voting = SampleDomains.getSampleOptions(voting1);
        LinkedList<Option> optionsToSaveFor2Voting = SampleDomains.getSampleOptions(voting2);
        optionsToSaveFor1Voting.forEach(option -> {
            optionRepository.save(option);
        });
        optionsToSaveFor2Voting.forEach(option -> {
            optionRepository.save(option);
        });

        assertThat(6).isEqualTo(optionRepository.findAll().size());
        assertThat(3).isEqualTo(optionRepository.findAllByVoting(voting1).size());
        assertThat(3).isEqualTo(optionRepository.findAllByVoting(voting2).size());
        assertThat(0).isEqualTo(optionRepository.findAllByVoting(voting3).size());
    }

}
