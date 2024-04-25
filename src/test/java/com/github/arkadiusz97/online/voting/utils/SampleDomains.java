package com.github.arkadiusz97.online.voting.utils;

import com.github.arkadiusz97.online.voting.domain.Option;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.github.arkadiusz97.online.voting.utils.Utils.getDateAheadOfDays;

public class SampleDomains {

    public static final Long DAY_AS_MILLISECONDS = 1000L * 60L * 60L * 24L;
    public static final Date NOW = new Date();
    public static final Long NOW_AS_MILLISECONDS = new Date().getTime();

    public static User getSampleUser() {
        return new User("some-mail1@domain.eu", "abc123", new Date());
    }

    public static UserDTO getSampleUserDTO() {
        String email = "some-mail1@domain.eu";
        List<RoleDTO> singleRoleDTO = Collections.singletonList(new RoleDTO("ROLE_USER"));
        return new UserDTO(1L, email, NOW, singleRoleDTO);
    }

    public static LinkedList<User> getSampleUsers() {
        LinkedList<User> sampleUsers = new LinkedList<>();
        sampleUsers.add(new User("some-mail1@domain.eu", "abc123", NOW));
        sampleUsers.add(new User("some-mail2@domain.eu", "abc", NOW));
        sampleUsers.add(new User("some-mail3@domain.eu", "abc999", NOW));
        sampleUsers.add(new User("some-mail4@domain.eu", "111", NOW));
        sampleUsers.add(new User("some-mail5@domain.eu", "eeee", NOW));
        return sampleUsers;
    }

    public static LinkedList<Voting> getSampleVotings(User user) {
        LinkedList<Voting> sampleVotings = new LinkedList<>();
        Date tomorrow = new Date(NOW.getTime() + (1000 * 60 * 60 * 24));

        sampleVotings.add(new Voting(0L, "voting 1", tomorrow, NOW, user));
        sampleVotings.add(new Voting(0L, "voting 2", tomorrow, NOW, user));
        sampleVotings.add(new Voting(0L, "voting 3", tomorrow, NOW, user));

        return sampleVotings;
    }

    public static CreateVotingDTO getSampleVotingDTO() {
        return new CreateVotingDTO("some voting", NOW, List.of("opt1, opt2, opt3"));
    }

    public static LinkedList<CreateVotingDTO> getSampleVotingDTOs() {
        LinkedList<CreateVotingDTO> sampleVotingDTOs = new LinkedList<>();
        sampleVotingDTOs.add(
                new CreateVotingDTO("some voting", getDateAheadOfDays(1), List.of("opt1, opt2, opt3"))
        );
        sampleVotingDTOs.add(
                new CreateVotingDTO("some voting 2", getDateAheadOfDays(1), List.of("opt4, opt5, opt6"))
        );
        sampleVotingDTOs.add(
                new CreateVotingDTO("some voting 3", getDateAheadOfDays(1), List.of("opt7, opt8, opt9"))
        );
        return sampleVotingDTOs;
    }

    public static VotingWithOptionsDTO getSampleVotingWithOptionsDTO() {
        return new VotingWithOptionsDTO(1L, "voting 1",
                getDateAheadOfDays(1), NOW, getSampleUserDTO(),
                getSampleOptionDTOs("opt1", "opt2", "opt3"));
    }

    public static LinkedList<VotingWithOptionsDTO> getSampleVotingWithOptionsDTOs() {
        LinkedList<VotingWithOptionsDTO> sampleVotingWithOptionsDTOs = new LinkedList<>();
        sampleVotingWithOptionsDTOs.add(new VotingWithOptionsDTO(1L, "voting 1",
                getDateAheadOfDays(1), NOW, getSampleUserDTO(),
                getSampleOptionDTOs("opt1", "opt2", "opt3"))
        );
        sampleVotingWithOptionsDTOs.add(new VotingWithOptionsDTO(2L, "voting 2",
                getDateAheadOfDays(2), getDateAheadOfDays(1), getSampleUserDTO(),
                getSampleOptionDTOs("opt4", "opt5", "opt6"))
        );
        sampleVotingWithOptionsDTOs.add(new VotingWithOptionsDTO(3L, "voting 3",
                getDateAheadOfDays(5),
                getDateAheadOfDays(3),
                getSampleUserDTO(), getSampleOptionDTOs("opt7", "opt8", "opt9"))
        );
        return sampleVotingWithOptionsDTOs;
    }

    public static LinkedList<Option> getSampleOptions(Voting voting) {
        LinkedList<Option> sampleOptions = new LinkedList<>();
        sampleOptions.add(new Option("opt1", voting));
        sampleOptions.add(new Option("opt2", voting));
        sampleOptions.add(new Option("opt3", voting));
        return sampleOptions;
    }

    public static LinkedList<OptionDTO> getSampleOptionDTOs(String opt1, String opt2, String opt3) {
        LinkedList<OptionDTO> sampleOptionDTOs = new LinkedList<>();
        sampleOptionDTOs.add(new OptionDTO(1L, opt1));
        sampleOptionDTOs.add(new OptionDTO(2L, opt2));
        sampleOptionDTOs.add(new OptionDTO(3L, opt3));
        return sampleOptionDTOs;
    }

    public static VotingSummaryDto getSampleVotingSummaryDto() {
        OptionResultDTO optionResultDTO = new OptionResultDTO("opt1", 2L, new BigDecimal(50));
        OptionResultDTO optionResultDTO2 = new OptionResultDTO("opt2", 2L, new BigDecimal(50));
        return new VotingSummaryDto("some voting", 4L, List.of(optionResultDTO, optionResultDTO2),
                List.of("opt1", "opt2"), true);
    }

}
