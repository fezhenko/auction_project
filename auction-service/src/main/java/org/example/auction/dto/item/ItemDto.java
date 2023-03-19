package org.example.auction.dto.item;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.example.auction.dto.category.CategoryDto;
import org.example.auction.dto.currency.CurrencyDto;

import java.util.Date;

@Value
@Jacksonized
@Builder
public class ItemDto {
    Long id;
    String description;
    String itemState;
    Double price;
    CurrencyDto currencyDto;
    CategoryDto categoryDto;
    Date createdAt;
}
