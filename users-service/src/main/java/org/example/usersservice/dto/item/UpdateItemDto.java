package org.example.usersservice.dto.item;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.usersservice.validator.ValidateItemStatuses;
import org.springframework.lang.Nullable;

@Value
@Builder
@Jacksonized
public class UpdateItemDto {
    @Nullable
    String description;
    @ValidateItemStatuses
    String itemStatus;
    @Nullable
    Double price;
    @Nullable
    Long itemCategory;
}
