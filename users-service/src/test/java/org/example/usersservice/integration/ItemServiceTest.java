package org.example.usersservice.integration;

import org.example.usersservice.dto.item.CreateItemDto;
import org.example.usersservice.dto.item.CreateItemResultDto;
import org.example.usersservice.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Test
    public void itemCreatedSuccess() {
        CreateItemResultDto expectedResult = CreateItemResultDto.builder().build();
        CreateItemDto body = CreateItemDto.builder().ownerId(1L).price(1000D).description("test").build();

        CreateItemResultDto actualResult = itemService.createItem(body);
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }
}
