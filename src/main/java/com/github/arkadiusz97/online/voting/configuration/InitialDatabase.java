package com.github.arkadiusz97.online.voting.configuration;

import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.repository.RoleRepository;
import com.github.arkadiusz97.online.voting.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InitialDatabase {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        roleRepository.save(new Role("ROLE_USER"));
        roleRepository.save(new Role("ROLE_ADMIN"));
        userService.registerNewAdmin("sample@domain.com");
    }

}
