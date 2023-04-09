package org.example.apigateway.dto.items;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class CreateItemResultDto {
    String message;
}
