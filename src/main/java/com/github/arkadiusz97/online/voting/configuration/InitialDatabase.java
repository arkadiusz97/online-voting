package com.github.arkadiusz97.online.voting.configuration;

import com.github.arkadiusz97.online.voting.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitialDatabase {
    private final UserService userService;

    @Autowired
    InitialDatabase(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        userService.registerNew("sample@domain.com");
    }
}
