package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String registerNew(String recipient) {
        User newUser =  new User(null, recipient, "autogeneratedpassword", new Date(), false);
        userRepository.save(newUser);
        String message = String.format("Registered a new user with %s email", recipient);
        logger.info(message);
        return message;
    }

    public void sendMessage(String recipient) {

    }

    public UserDTO show(Long id) {
        return getDTO(userRepository.findById(id).get());//todo chage get
    }

    public List<UserDTO> showMany(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository
            .findAll(pageable)
            .stream()
            .map(this::getDTO)
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
        logger.info("Removed user with {} id", id);
    }

    private UserDTO getDTO(final User user) {
        return new UserDTO(
            user.getId(),
            user.getEmail(),
            user.getCreated(),
            user.getIsAdmin()
        );
    }
}
