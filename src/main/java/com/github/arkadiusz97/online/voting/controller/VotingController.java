package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.requestbody.VoteDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.GenericResponse;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.service.VotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        GenericResponse result = new GenericResponse(
            votingService.create(createVotingDTO)
        );
        return new ResponseEntity<GenericResponse>(result, HttpStatusCode.valueOf(201));
    }

    @GetMapping("get")
    public ResponseEntity<List<VotingWithOptionsDTO>> showMany(@RequestParam String pageNumber, @RequestParam String pageSize) {
        List<VotingWithOptionsDTO> result =
            votingService.showMany(Integer.valueOf(pageNumber), Integer.valueOf(pageSize));
        return new ResponseEntity<List<VotingWithOptionsDTO>>(result, HttpStatusCode.valueOf(200));
    }

    @GetMapping("get/{id}")
    public ResponseEntity<VotingWithOptionsDTO> show(@PathVariable String id) {
        VotingWithOptionsDTO result = votingService.get(Long.valueOf(id));
        return new ResponseEntity<VotingWithOptionsDTO>(result, HttpStatusCode.valueOf(200));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable String id) {
        votingService.delete(Long.valueOf(id));
        GenericResponse result = new GenericResponse("Voting " + id + " deleted");
        return new ResponseEntity<GenericResponse>(result, HttpStatusCode.valueOf(200));
    }

    @PostMapping("vote")
    public ResponseEntity<GenericResponse> vote(@RequestBody VoteDTO voteDTO) {
        votingService.vote(voteDTO.optionId());
        GenericResponse result = new GenericResponse("User voted");
        return new ResponseEntity<GenericResponse>(result, HttpStatusCode.valueOf(200));
    }
}
