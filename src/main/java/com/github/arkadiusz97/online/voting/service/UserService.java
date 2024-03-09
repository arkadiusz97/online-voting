package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;

import java.util.List;

public interface UserService {
    public String registerNewUser(String recipient);
    public String registerNewAdmin(String recipient);
    String registerNew(String recipient, String role);
    void sendMessage(String recipient);
    UserDTO show(Long id);
    List<UserDTO> showMany(Integer pageNumber, Integer pageSize);
    void delete(Long id);
}
