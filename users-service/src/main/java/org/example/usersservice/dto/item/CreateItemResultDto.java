package org.example.usersservice.dto.item;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class CreateItemResultDto {
    String message;
}
