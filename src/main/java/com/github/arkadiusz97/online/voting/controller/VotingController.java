package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.requestbody.VoteDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.GenericResponse;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.service.VotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ROLE_USER')")//todo move to service
@RestController
@RequestMapping("voting")
@RequiredArgsConstructor
public class VotingController {
    private final VotingService votingService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("create")
    public ResponseEntity<GenericResponse> create(@RequestBody CreateVotingDTO createVotingDTO) {
        votingService.create(createVotingDTO);
        GenericResponse result = new GenericResponse("created");
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("get")
    public ResponseEntity<List<VotingWithOptionsDTO>> showMany(@RequestParam String pageNumber, @RequestParam String pageSize) {
        List<VotingWithOptionsDTO> result =
            votingService.showMany(Integer.valueOf(pageNumber), Integer.valueOf(pageSize));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<VotingWithOptionsDTO> show(@PathVariable String id) {
        VotingWithOptionsDTO result = votingService.get(Long.valueOf(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable String id) {
        votingService.delete(Long.valueOf(id));
        GenericResponse result = new GenericResponse("Voting " + id + " deleted");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("vote")
    public ResponseEntity<GenericResponse> vote(@RequestBody VoteDTO voteDTO) {
        votingService.vote(voteDTO.optionId());
        GenericResponse result = new GenericResponse("User voted");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
