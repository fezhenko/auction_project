package org.example.usersservice.dto.item;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateItemResultDto {
    String message;
}
