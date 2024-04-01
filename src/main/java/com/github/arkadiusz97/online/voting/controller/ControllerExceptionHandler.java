package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.responsebody.GenericResponseDTO;
import com.github.arkadiusz97.online.voting.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponseDTO> handleException(Exception e) {
        GenericResponseDTO response = new GenericResponseDTO("Internal server error: " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponseDTO> handleResourceNotFoundException() {
        GenericResponseDTO response = new GenericResponseDTO("Resource not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyVotedException.class)
    public ResponseEntity<GenericResponseDTO> handleUserAlreadyVotedException() {
        GenericResponseDTO response = new GenericResponseDTO("User has already voted in this voting");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OptionNotFoundException.class)
    public ResponseEntity<GenericResponseDTO> handleOptionNotFoundException() {
        GenericResponseDTO response = new GenericResponseDTO("Option not found");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<GenericResponseDTO> handleUserAlreadyExistsException() {
        GenericResponseDTO response = new GenericResponseDTO("User already exists");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VotingIsExpiredException.class)
    public ResponseEntity<GenericResponseDTO> handleVotingIsExpiredException() {
        GenericResponseDTO response = new GenericResponseDTO("Voting is expired");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VotingEndDateIsBehindTodayException.class)
    public ResponseEntity<GenericResponseDTO> handleVotingEndDateIsBehindTodayException() {
        GenericResponseDTO response = new GenericResponseDTO("Voting end date is behind today");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
