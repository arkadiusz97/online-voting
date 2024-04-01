package com.github.arkadiusz97.online.voting.dto.responsebody;

import java.util.List;

public record VotingSummaryDto(String VotingDescription, Long totalVotes, List<OptionResultDTO> optionResults,
    List<String> winningOptions, Boolean isFinished) { }
