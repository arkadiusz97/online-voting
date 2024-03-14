package com.github.arkadiusz97.online.voting.utils;

import com.github.arkadiusz97.online.voting.domain.User;

import java.util.Date;
import java.util.LinkedList;

public class SampleDomains {
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
}
