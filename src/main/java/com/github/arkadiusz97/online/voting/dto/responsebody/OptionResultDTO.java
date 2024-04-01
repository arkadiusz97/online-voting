package com.github.arkadiusz97.online.voting.dto.responsebody;

import java.math.BigDecimal;

public record OptionResultDTO(String optionDescription, Long numberOfChoices, BigDecimal percentageOfChoices) {
}
