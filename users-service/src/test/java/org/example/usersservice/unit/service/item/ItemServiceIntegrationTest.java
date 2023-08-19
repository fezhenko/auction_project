package org.example.usersservice.unit.service.item;

import org.example.usersservice.dto.item.CreateItemDto;
import org.example.usersservice.repository.CategoryRepository;
import org.example.usersservice.repository.ItemRepository;
import org.example.usersservice.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class ItemServiceIntegrationTest extends AbstractTestContainersTest {

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    private ItemService itemService;

    @Test
    void testCreateItemWithDescription() {
        CreateItemDto itemDto = CreateItemDto.builder().ownerId(1L).price(100d).description("Test description").build();

        // Mock the itemRepository method using when()
        doNothing().when(itemRepository).createItem(anyLong(), anyDouble(), anyString());

        // Call the createItem method
        itemService.createItem(itemDto);

        // Verify that the repository method was called with the correct arguments
        verify(itemRepository).createItem(eq(itemDto.getOwnerId()), eq(itemDto.getPrice()), eq(itemDto.getDescription()));
    }
}
