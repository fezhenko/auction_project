package org.example.usersservice.converter;

import java.util.ArrayList;
import java.util.List;

import org.example.usersservice.dto.category.CategoryDto;
import org.example.usersservice.dto.currency.CurrencyDto;
import org.example.usersservice.dto.currency.FullCurrencyInfoDto;
import org.example.usersservice.dto.item.ItemDto;
import org.example.usersservice.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Mapper
public interface ItemConverter {

    @Mapping(source = "Item", target = "ItemDto", qualifiedByName = "ItemToItemDto")
    @Named("ItemToItemDto")
    default ItemDto toDto(Item item) throws RuntimeException {
        ObjectMapper objectMapper = new JsonMapper();
        return ItemDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .itemState(item.getItemState())
                .price(item.getPrice())
                .currencyDto(FullCurrencyInfoDto.builder()
                        .id(item.getCurrency())
                        .currency(objectMapper.readValue(item.getCurrencyDesc().toString(), CurrencyDto.class))
                        .build())
                .categoryDto(CategoryDto.builder()
                        .id(item.getItemCategory())
                        .categoryName(item.getCategoryName())
                        .build())
                .createdAt(item.getCreatedAt())
                .build();
    }

    @Mapping(source = "List<Item>", target = "List<ItemDto>", qualifiedByName = "ItemToItemDto")
    @Named("ItemToItemDto")
    default List<ItemDto> toDto(List<Item> items) throws RuntimeException {
        ObjectMapper objectMapper = new JsonMapper();
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items) {
            ItemDto convertedItem = ItemDto.builder()
                    .id(item.getId())
                    .description(item.getDescription())
                    .itemState(item.getItemState())
                    .price(item.getPrice())
                    .currencyDto(FullCurrencyInfoDto.builder()
                            .id(item.getCurrency())
                            .currency(objectMapper.readValue(
                                    item.getCurrencyDesc().toString(), CurrencyDto.class))
                            .build())
                    .categoryDto(CategoryDto.builder()
                            .id(item.getItemCategory())
                            .categoryName(item.getCategoryName())
                            .build())
                    .createdAt(item.getCreatedAt())
                    .build();
            itemDtoList.add(convertedItem);
        }
        return itemDtoList;
    }
}
