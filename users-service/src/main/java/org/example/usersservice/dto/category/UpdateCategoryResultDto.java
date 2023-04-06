package org.example.usersservice.dto.category;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UpdateCategoryResultDto {
    String message;
}
