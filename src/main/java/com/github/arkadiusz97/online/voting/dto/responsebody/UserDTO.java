package com.github.arkadiusz97.online.voting.dto.responsebody;

import java.util.Date;

public record UserDTO(Long id, String email, Date created, Boolean isAdmin) {
}
