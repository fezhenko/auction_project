package org.example.usersservice.dto.category;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class CategoryDto {
    Long id;
    String categoryName;
}
