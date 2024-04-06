package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.Role;
import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserRole;
import com.github.arkadiusz97.online.voting.domain.UserRoleKey;
import com.github.arkadiusz97.online.voting.dto.responsebody.RoleDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.exception.NotSecurePasswordException;
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
import java.util.regex.Pattern;
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
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGeneratorService passwordGeneratorService;

    @Override
    public String registerNewUser(String recipient) {
        logger.debug("Created a new user {}", recipient);
        return registerNew(recipient, "ROLE_USER");
    }

    @Override
    public String registerNewAdmin(String recipient) {
        logger.debug("Created a new admin {}", recipient);
        return registerNew(recipient, "ROLE_ADMIN");
    }

    @Override
    public String registerNew(String recipient, String roleStr) {
        String randomPassword = passwordGeneratorService.generatePassword();
        return registerNew(recipient, roleStr, randomPassword);
    }

    @Override
    public String registerNew(String recipient, String roleStr, String password) {

        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(recipient));
        existingUser.ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        User newUser =  new User(recipient, passwordEncoder.encode(password), new Date());
        userRepository.save(newUser);

        Role role = roleRepository.findByName(roleStr);
        UserRoleKey userRoleKey = new UserRoleKey(newUser.getId(), role.getId());
        UserRole userRole = new UserRole(userRoleKey, newUser, role);
        userRoleRepository.save(userRole);

        String messageContent = String.format("Hello,\nYour passowrd to account in Online Voting is: %s\nRegards",
            password);
        mailService.sendEmail(recipient, "Credentials to your account in Online Voting", messageContent);

        String message = String.format("Registered a new user with %s email", recipient);
        logger.debug(message);
        return message;
    }

    @Override
    public UserDTO getById(Long id) {
        logger.debug("Called getById with id {}", id);
        return getDTO(userRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @Override
    public UserDTO getByEmail(String email) {
        logger.debug("Called getByEmail with email {}", email);
        return getDTO(userRepository.findByEmail(email));
    }

    @Override
    public List<UserDTO> showMany(Integer pageNumber, Integer pageSize) {
        logger.debug("Called showMany with pageNumber {} and pageSize {}", pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository
            .findAll(pageable)
            .stream()
            .map(this::getDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if(userRepository.existsById(id)) {
            userRoleRepository.deleteAllByUser(userRepository.findById(id).get());
            userRepository.deleteById(id);
            logger.debug("Removed user with {} id", id);
        } else {
            logger.debug("User with {} id not exists", id);
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logger.debug("Called getCurrentUser with email {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public void changeCurrentUserPassword(String newPassword) {
        if(validatePassword(newPassword) == false) {
            throw new NotSecurePasswordException();
        }
        User currentUser = getCurrentUser();
        logger.debug("Changing password for user {}", currentUser.getEmail());
        String newPasswordEncoded = passwordEncoder.encode(newPassword);
        currentUser.setPassword(newPasswordEncoded);
        userRepository.save(currentUser);
    }

    @Override
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

    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}");
        return pattern.matcher(password).matches();
    }

}
