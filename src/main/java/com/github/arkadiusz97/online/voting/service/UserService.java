package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;

import java.util.List;

public interface UserService {
    String registerNew(String recipient);
    void sendMessage(String recipient);
    UserDTO show(Long id);
    List<UserDTO> showMany(Integer integer, Integer valueOf);
    void delete(Long id);
}
