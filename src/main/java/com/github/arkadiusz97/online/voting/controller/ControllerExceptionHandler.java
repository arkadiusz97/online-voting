package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.responsebody.GenericResponse;
import com.github.arkadiusz97.online.voting.exception.OptionNotFoundException;
import com.github.arkadiusz97.online.voting.exception.ResourceNotFoundException;
import com.github.arkadiusz97.online.voting.exception.UserAlreadyExistsException;
import com.github.arkadiusz97.online.voting.exception.UserAlreadyVotedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleException(Exception e) {
        GenericResponse response = new GenericResponse("Internal server error: " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponse> handleResourceNotFoundException() {
        GenericResponse response = new GenericResponse("Resource not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyVotedException.class)
    public ResponseEntity<GenericResponse> handleUserAlreadyVotedException() {
        GenericResponse response = new GenericResponse("User has already voted in this voting");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OptionNotFoundException.class)
    public ResponseEntity<GenericResponse> handleOptionNotFoundException() {
        GenericResponse response = new GenericResponse("Option not found");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<GenericResponse> handleUserAlreadyExistsException() {
        GenericResponse response = new GenericResponse("User already exists");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
