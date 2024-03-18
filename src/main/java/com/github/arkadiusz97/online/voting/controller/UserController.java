package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.responsebody.GenericResponse;
import com.github.arkadiusz97.online.voting.dto.responsebody.UserDTO;
import com.github.arkadiusz97.online.voting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@PreAuthorize("hasRole('ROLE_ADMIN')")//todo move to service
@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("register-new")
    public ResponseEntity<GenericResponse> registerNew(@RequestBody String someStr) {
        GenericResponse result = new GenericResponse(
            userService.registerNewUser(someStr)
        );
        return new ResponseEntity<GenericResponse>(result, HttpStatusCode.valueOf(200));
    }

    @GetMapping("show")
    public ResponseEntity<List<UserDTO>> showMany(@RequestParam String pageNumber, @RequestParam String pageSize) {
        List<UserDTO> result = userService.showMany(Integer.valueOf(pageNumber), Integer.valueOf(pageSize));
        return new ResponseEntity<List<UserDTO>>(result, HttpStatusCode.valueOf(200));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("show-current-user")
    public ResponseEntity<UserDTO> showCurrentUser(Principal principal) {
        String userEmail = principal.getName();
        UserDTO result = userService.getByEmail(userEmail);
        return new ResponseEntity<UserDTO>(result, HttpStatusCode.valueOf(200));
    }

    @GetMapping("show/{id}")
    public ResponseEntity<UserDTO> show(@PathVariable String id) {
        UserDTO result = userService.getById(Long.valueOf(id));
        return new ResponseEntity<UserDTO>(result, HttpStatusCode.valueOf(200));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable String id) {
        userService.delete(Long.valueOf(id));
        GenericResponse result = new GenericResponse("User " + id + "deleted");
        return new ResponseEntity<GenericResponse>(result, HttpStatusCode.valueOf(200));
    }
}
