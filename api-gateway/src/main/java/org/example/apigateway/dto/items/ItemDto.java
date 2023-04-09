package org.example.apigateway.dto.items;

import java.util.Date;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.apigateway.dto.category.CategoryDto;
import org.example.apigateway.dto.currency.FullCurrencyInfoDto;


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
