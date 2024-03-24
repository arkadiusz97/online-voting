package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;

import java.util.List;

public interface UserService {
    public String registerNewUser(String recipient);
    public String registerNewAdmin(String recipient);
    String registerNew(String recipient, String role);
    void sendMessage(String recipient);
    UserDTO getById(Long id);
    UserDTO getByEmail(String email);
    List<UserDTO> showMany(Integer pageNumber, Integer pageSize);
    void delete(Long id);
    User getCurrentUser();
    public UserDTO getDTO(final User user);
}
