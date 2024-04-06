package com.github.arkadiusz97.online.voting.repository;

import com.github.arkadiusz97.online.voting.domain.Option;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserOption;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.utils.SampleDomains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserOptionRepositoryTest {

    @Autowired
    private UserOptionRepository userOptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Test
    public void whenfindAllByUser_ThenReturnWantedUsers() {
        LinkedList<User> usersToSave = SampleDomains.getSampleUsers();
        usersToSave.forEach(user -> {
            userRepository.save(user);
        });

        LinkedList<Voting> votingsToSave = SampleDomains.getSampleVotings(usersToSave.get(0));
        votingsToSave.forEach(voting -> {
            votingRepository.save(voting);
        });

        Voting voting1 = votingRepository.findAll().get(0);
        Option option1 = optionRepository.save(
            new Option("opt1", voting1)
        );
        User user1 = userRepository.findAll().get(0);
        User user2 = userRepository.findAll().get(1);
        User user3 = userRepository.findAll().get(3);

        userOptionRepository.save(new UserOption(user1, option1));
        userOptionRepository.save(new UserOption(user2, option1));

        assertThat(2).isEqualTo(userOptionRepository.findAll().size());
        assertThat(1).isEqualTo(userOptionRepository.findAllByUser(user1).size());
        assertThat(1).isEqualTo(userOptionRepository.findAllByUser(user2).size());
        assertThat(0).isEqualTo(userOptionRepository.findAllByUser(user3).size());

    }

}
