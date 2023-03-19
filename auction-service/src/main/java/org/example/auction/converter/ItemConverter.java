package org.example.auction.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.auction.dto.category.CategoryDto;
import org.example.auction.dto.currency.CurrencyDto;
import org.example.auction.dto.currency.FullCurrencyInfoDto;
import org.example.auction.dto.item.ItemDto;
import org.example.auction.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ItemConverter {

    ItemDto toDto(Item item);

    @Mapping(source = "Item", target = "ItemDto", qualifiedByName = "ItemToItemDto")
    @Named("ItemToItemDto")
    default List<ItemDto> toDto(List<Item> items) throws RuntimeException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items) {
            ItemDto convertedItem = ItemDto.builder()
                    .id(item.getId())
                    .description(item.getDescription())
                    .price(item.getPrice())
                    .currencyDto(
                            FullCurrencyInfoDto.builder()
                                    .id(item.getCurrency())
                                    .currency(
                                            objectMapper.readValue(item.getCurrencyDesc().toString(), CurrencyDto.class)
                                    )
                                    .build()
                    )
                    .categoryDto(
                            CategoryDto.builder()
                                    .id(item.getItemCategory())
                                    .name(item.getCategoryName())
                                    .build()
                    )
                    .createdAt(item.getCreatedAt())
                    .build();
            itemDtoList.add(convertedItem);
        }
        return itemDtoList;
    }
}
