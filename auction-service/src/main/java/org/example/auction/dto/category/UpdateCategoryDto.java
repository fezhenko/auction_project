package org.example.auction.dto.category;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class UpdateCategoryDto {
    String name;
}
