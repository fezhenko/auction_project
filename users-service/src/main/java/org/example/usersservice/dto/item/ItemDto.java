package org.example.usersservice.dto.item;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.usersservice.dto.category.CategoryDto;
import org.example.usersservice.dto.currency.FullCurrencyInfoDto;


import java.util.Date;

@Value
@Jacksonized
@Builder
public class ItemDto {
    Long id;
    String description;
    String itemState;
    Double price;
    FullCurrencyInfoDto currencyDto;
    CategoryDto categoryDto;
    Date createdAt;
}
