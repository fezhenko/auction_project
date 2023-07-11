package org.example.usersservice.unit.service.item;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;

import org.example.usersservice.dto.item.CreateItemDto;
import org.example.usersservice.model.Item;
import org.example.usersservice.repository.ItemRepository;
import org.example.usersservice.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemService itemService;

    @Test
    public void should_create_item_not_null_description() {
        // given
        CreateItemDto createItem = generateCreateItemDto();

        // when
        itemService.createItem(createItem);

        // then
        verify(itemRepository, times(1))
            .createItem(eq(createItem.getOwnerId()), eq(createItem.getPrice()), eq(createItem.getDescription()));
    }

    @Test
    public void should_create_item_null_description() {
        // given
        CreateItemDto createItem = generateCreateItemDtoWithoutDescription();

        // when
        itemService.createItem(createItem);

        // then
        verify(itemRepository, times(1))
            .createItem(eq(createItem.getOwnerId()), eq(createItem.getPrice()));
    }

    @Test
    public void shouldThrowDatabaseException() {
        // given
        CreateItemDto createItem = generateCreateItemDto();
        Mockito.doThrow(new RuntimeException("Database is down")).when(itemRepository)
            .findItemById(createItem.getOwnerId());

        //then
        assertThrows(RuntimeException.class, () -> itemService.findItemById(createItem.getOwnerId()),
            "Database is down");
    }

    private CreateItemDto generateCreateItemDto() {
        return CreateItemDto.builder().ownerId(777L).price(1000D).description("test").build();
    }

    private CreateItemDto generateCreateItemDtoWithoutDescription() {
        return CreateItemDto.builder().ownerId(777L).price(1000D).description(null).build();
    }

    private Item generateItem(long ownedId, double price, String description) {
        return Item.builder()
            .id(998L)
            .description(description)
            .price(price)
            .currency(3L)
            .currencyDesc("USD")
            .itemCategory(1L)
            .categoryName("cat name")
            .itemState("ON_SOLD")
            .createdAt(Date.valueOf(LocalDate.now(Clock.systemUTC())))
            .ownerId(ownedId)
            .build();
    }
}
