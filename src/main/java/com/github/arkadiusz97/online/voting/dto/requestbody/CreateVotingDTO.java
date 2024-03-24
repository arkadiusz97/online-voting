package com.github.arkadiusz97.online.voting.dto.requestbody;

import java.util.Date;
import java.util.List;

public record CreateVotingDTO(String description, Date endDate, List<String> options) {
}
