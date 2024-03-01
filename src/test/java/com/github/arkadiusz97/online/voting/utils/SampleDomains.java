package com.github.arkadiusz97.online.voting.utils;

import com.github.arkadiusz97.online.voting.domain.User;

import java.util.Date;
import java.util.LinkedList;

public class SampleDomains {
    public static LinkedList<User> getSampleUsers() {
        LinkedList<User> sampleUsers = new LinkedList<>();
        Date now = new Date();
        sampleUsers.add(new User(1L, "some-mail1@domain.eu", "abc123", now, false));
        sampleUsers.add(new User(2L, "some-mail2@domain.eu", "abc", now, true));
        sampleUsers.add(new User(3L, "some-mail3@domain.eu", "abc999", now, false));
        sampleUsers.add(new User(4L, "some-mail4@domain.eu", "111", now, true));
        sampleUsers.add(new User(5L, "some-mail5@domain.eu", "eeee", now, false));
        return sampleUsers;
    }
}
