package com.github.arkadiusz97.online.voting.dto.responsebody;

import com.github.arkadiusz97.online.voting.domain.Option;
import com.github.arkadiusz97.online.voting.domain.User;

import java.util.Date;
import java.util.List;

public record VotingWithOptionsDTO(Long id, String description, Date endDate, Date createdDate,
    UserDTO createdBy, List<OptionDTO> votingOptions) { }
