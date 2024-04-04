package com.github.arkadiusz97.online.voting.configuration;

import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.repository.RoleRepository;
import com.github.arkadiusz97.online.voting.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InitialDatabase {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final String defaultAdminLogin;
    private final String defaultAdminPassword;

    public InitialDatabase(UserService userService, RoleRepository roleRepository,
            @Value("${online-voting.default-admin-login}") String defaultAdminLogin,
            @Value("${online-voting.default-admin-password}") String defaultAdminPassword) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.defaultAdminLogin = defaultAdminLogin;
        this.defaultAdminPassword = defaultAdminPassword;
    }

    @PostConstruct
    public void init() {
        roleRepository.save(new Role("ROLE_USER"));
        roleRepository.save(new Role("ROLE_ADMIN"));
        userService.registerNew(defaultAdminLogin, "ROLE_ADMIN", defaultAdminPassword);
    }

}
