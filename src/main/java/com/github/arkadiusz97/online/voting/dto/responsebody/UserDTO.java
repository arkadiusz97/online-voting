package com.github.arkadiusz97.online.voting.dto.responsebody;

import java.util.Date;
import java.util.List;

public record UserDTO(Long id, String email, Date created, List<RoleDTO> roles) {
}
