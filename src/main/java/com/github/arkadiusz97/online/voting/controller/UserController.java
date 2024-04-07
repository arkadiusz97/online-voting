package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.requestbody.ChangePasswordDTO;
import com.github.arkadiusz97.online.voting.dto.requestbody.NewUserDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.GenericResponseDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<GenericResponseDTO> registerNew(@RequestBody NewUserDTO dto) {
        GenericResponseDTO result = new GenericResponseDTO(
            userService.registerNewUser(dto.recipient())
        );
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("get")
    public ResponseEntity<List<UserDTO>> showMany(@RequestParam String pageNumber, @RequestParam String pageSize) {
        List<UserDTO> result = userService.showMany(Integer.valueOf(pageNumber), Integer.valueOf(pageSize));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("get-current")
    public ResponseEntity<UserDTO> showCurrentUser(Principal principal) {
        String userEmail = principal.getName();
        UserDTO result = userService.getByEmail(userEmail);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<UserDTO> show(@PathVariable String id) {
        UserDTO result = userService.getById(Long.valueOf(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<GenericResponseDTO> delete(@PathVariable String id) {
        userService.delete(Long.valueOf(id));
        GenericResponseDTO result = new GenericResponseDTO("User " + id + " deleted");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("change-password")
    public ResponseEntity<Void> registerNew(@RequestBody ChangePasswordDTO dto) {
        userService.changeCurrentUserPassword(dto.newPassword());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
