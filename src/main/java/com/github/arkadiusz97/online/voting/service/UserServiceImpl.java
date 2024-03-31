package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserRole;
import com.github.arkadiusz97.online.voting.domain.UserRoleKey;
import com.github.arkadiusz97.online.voting.dto.responsebody.RoleDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.exception.ResourceNotFoundException;
import com.github.arkadiusz97.online.voting.exception.UserAlreadyExistsException;
import com.github.arkadiusz97.online.voting.repository.RoleRepository;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import com.github.arkadiusz97.online.voting.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerNewUser(String recipient) {
        return registerNew(recipient, "ROLE_USER");
    }

    public String registerNewAdmin(String recipient) {
        return registerNew(recipient, "ROLE_ADMIN");
    }

    public String registerNew(String recipient, String roleStr) {
        //todo generate random password or lock account before setting password "autogeneratedpassword"
        String tmp = "autogeneratedpassword";

        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(recipient));
        existingUser.ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        User newUser =  new User(recipient, passwordEncoder.encode(tmp), new Date());
        userRepository.save(newUser);

        Role role = roleRepository.findByName(roleStr);
        UserRoleKey userRoleKey = new UserRoleKey(newUser.getId(), role.getId());
        UserRole userRole = new UserRole(userRoleKey, newUser, role);
        userRoleRepository.save(userRole);

        String message = String.format("Registered a new user with %s email", recipient);
        logger.info(message);
        return message;
    }

    public void sendMessage(String recipient) {
        //todo implement
    }

    public UserDTO getById(Long id) {
        return getDTO(userRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    public UserDTO getByEmail(String email) {
        return getDTO(userRepository.findByEmail(email));//todo chage get
    }

    public List<UserDTO> showMany(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository
            .findAll(pageable)
            .stream()
            .map(this::getDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if(userRepository.existsById(id)) {
            userRoleRepository.deleteAllByUser(userRepository.findById(id).get());
            userRepository.deleteById(id);
            logger.info("Removed user with {} id", id);
        } else {
            logger.warn("User with {} id not exists", id);
            throw new ResourceNotFoundException();
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName());
    }

    public UserDTO getDTO(final User user) {
        Set<UserRole> userRoles = user.getUsersRoles();
        Stream<UserRole> userRolesStream = Optional.ofNullable(userRoles)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
        return new UserDTO(
            user.getId(),
            user.getEmail(),
            user.getCreated(),
            userRolesStream
                .map(this::getRoleDTO)
                .collect(Collectors.toList())
        );
    }

    private RoleDTO getRoleDTO(final UserRole userRole) {
        return new RoleDTO(
            userRole.getRole().getName()
        );
    }
}
