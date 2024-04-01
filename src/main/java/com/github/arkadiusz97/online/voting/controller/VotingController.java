package com.github.arkadiusz97.online.voting.controller;

import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.requestbody.VoteDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.GenericResponseDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingSummaryDto;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;
import com.github.arkadiusz97.online.voting.service.VotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<GenericResponseDTO> create(@RequestBody CreateVotingDTO createVotingDTO) {
        votingService.create(createVotingDTO);
        GenericResponseDTO result = new GenericResponseDTO("created");
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
    public ResponseEntity<GenericResponseDTO> delete(@PathVariable String id) {
        votingService.delete(Long.valueOf(id));
        GenericResponseDTO result = new GenericResponseDTO("Voting " + id + " deleted");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("vote")
    public ResponseEntity<GenericResponseDTO> vote(@RequestBody VoteDTO voteDTO) {
        votingService.vote(voteDTO.optionId());
        GenericResponseDTO result = new GenericResponseDTO("User voted");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("result/{id}")
    public ResponseEntity<VotingSummaryDto> getVotingResult(@PathVariable String id) {
        VotingSummaryDto result = votingService.getVotingResult(Long.valueOf(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
