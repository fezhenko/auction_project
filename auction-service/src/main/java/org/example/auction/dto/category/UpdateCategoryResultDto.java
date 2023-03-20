package org.example.auction.dto.category;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UpdateCategoryResultDto {
    String message;
}
