package org.example.usersservice.integration.item;

import org.example.usersservice.repository.CategoryRepository;
import org.example.usersservice.repository.ItemRepository;
import org.example.usersservice.service.ItemService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ItemServiceTestConfig {

    @Bean
    public ItemService itemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        return new ItemService(itemRepository, categoryRepository);
    }
}
