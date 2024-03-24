package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;

import java.util.List;

public interface VotingService {
    String create(CreateVotingDTO createVotingDTO);
    VotingWithOptionsDTO get(Long id);
    List<VotingWithOptionsDTO> showMany(Integer pageNumber, Integer pageSize);
    String vote(Long votingId, Long optionId);
    String delete(Long votingId);
}
