package com.github.arkadiusz97.online.voting.utils;

import com.github.arkadiusz97.online.voting.domain.Option;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.Voting;
import com.github.arkadiusz97.online.voting.dto.responsebody.RoleDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SampleDomains {

    public static User getSampleUser() {
        return new User("some-mail1@domain.eu", "abc123", new Date());
    }

    public static UserDTO getSampleUserDTO() {
        String email = "some-mail1@domain.eu";
        Date now = new Date();
        List<RoleDTO> singleRoleDTO = Collections.singletonList(new RoleDTO("ROLE_USER"));
        return new UserDTO(1L, email, now, singleRoleDTO);
    }

    public static LinkedList<User> getSampleUsers() {
        LinkedList<User> sampleUsers = new LinkedList<>();
        Date now = new Date();
        sampleUsers.add(new User("some-mail1@domain.eu", "abc123", now));
        sampleUsers.add(new User("some-mail2@domain.eu", "abc", now));
        sampleUsers.add(new User("some-mail3@domain.eu", "abc999", now));
        sampleUsers.add(new User("some-mail4@domain.eu", "111", now));
        sampleUsers.add(new User("some-mail5@domain.eu", "eeee", now));
        return sampleUsers;
    }

    public static LinkedList<Voting> getSampleVotings(User user) {
        LinkedList<Voting> sampleVotings = new LinkedList<>();
        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + (1000 * 60 * 60 * 24));

        sampleVotings.add(new Voting(0L, "voting 1", tomorrow, now, user));
        sampleVotings.add(new Voting(0L, "voting 2", tomorrow, now, user));
        sampleVotings.add(new Voting(0L, "voting 3", tomorrow, now, user));

        return sampleVotings;
    }

    public static LinkedList<Option> getSampleOptions(Voting voting) {
        LinkedList<Option> sampleOptions = new LinkedList<>();
        sampleOptions.add(new Option("opt1", voting));
        sampleOptions.add(new Option("opt2", voting));
        sampleOptions.add(new Option("opt3", voting));
        return sampleOptions;
    }

}
