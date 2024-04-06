package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;

import java.util.List;

public interface UserService {
    String registerNewUser(String recipient);
    String registerNewAdmin(String recipient);
    String registerNew(String recipient, String role);
    String registerNew(String recipient, String role, String password);
    UserDTO getById(Long id);
    UserDTO getByEmail(String email);
    List<UserDTO> showMany(Integer pageNumber, Integer pageSize);
    void delete(Long id);
    User getCurrentUser();
    void changeCurrentUserPassword(String newPassword);
    UserDTO getDTO(final User user);
}
