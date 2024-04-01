package com.github.arkadiusz97.online.voting.service;

import com.github.arkadiusz97.online.voting.dto.requestbody.CreateVotingDTO;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingSummaryDto;
import com.github.arkadiusz97.online.voting.dto.responsebody.VotingWithOptionsDTO;

import java.util.List;

public interface VotingService {
    void create(CreateVotingDTO createVotingDTO);
    VotingWithOptionsDTO get(Long id);
    List<VotingWithOptionsDTO> showMany(Integer pageNumber, Integer pageSize);
    void vote(Long optionId);
    void delete(Long votingId);
    VotingSummaryDto getVotingResult(Long votingId);
}
