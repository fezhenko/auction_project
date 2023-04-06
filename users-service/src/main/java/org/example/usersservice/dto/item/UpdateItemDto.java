package org.example.usersservice.dto.item;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.usersservice.validator.ValidateItemStatuses;

@Value
@Builder
@Jacksonized
public class UpdateItemDto {
    String description;
    @ValidateItemStatuses
    String itemStatus;
    Double price;
    Long itemCategory;
}
