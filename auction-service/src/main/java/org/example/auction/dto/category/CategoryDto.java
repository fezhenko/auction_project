package org.example.auction.dto.category;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class CategoryDto {
    Long id;
    String name;
}
