package org.example.usersservice.integration.item;

import org.example.usersservice.dto.item.CreateItemDto;
import org.example.usersservice.model.Item;
import org.example.usersservice.repository.CategoryRepository;
import org.example.usersservice.repository.ItemRepository;
import org.example.usersservice.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ItemServiceTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceTest {

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    @Autowired
    private ItemService itemService;

    @Test
    public void shouldCreateItemHappyPath() {
        CreateItemDto createItem = CreateItemDto.builder().ownerId(777L).price(1000D).description("test").build();
        Item item = Item.builder()
                .id(998L)
                .description(createItem.getDescription())
                .price(createItem.getPrice())
                .currency(3L)
                .currencyDesc("USD")
                .itemCategory(1L)
                .categoryName("cat name")
                .itemState("ON_SOLD")
                .createdAt(Date.valueOf(LocalDate.now(Clock.systemUTC())))
                .ownerId(createItem.getOwnerId())
                .build();
        itemService.createItem(createItem);
        Mockito.doReturn(item).when(itemRepository).findItemById(createItem.getOwnerId());

        assertNotNull(itemRepository.findItemByOwnerId(item.getOwnerId()));
    }

    @Test
    public void shouldThrowDatabaseException() {
        CreateItemDto createItem = CreateItemDto.builder().ownerId(777L).price(1000D).description("test").build();
        Mockito.doThrow(new RuntimeException("Database is down")).when(itemRepository).findItemById(createItem.getOwnerId());
        assertThrows(RuntimeException.class, () -> itemRepository.findItemById(createItem.getOwnerId()), "Database is down");
    }
}
