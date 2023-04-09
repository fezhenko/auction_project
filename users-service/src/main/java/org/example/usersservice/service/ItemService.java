package org.example.usersservice.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usersservice.dto.item.CreateItemDto;
import org.example.usersservice.dto.item.CreateItemResultDto;
import org.example.usersservice.dto.item.UpdateItemResultDto;
import org.example.usersservice.model.Category;
import org.example.usersservice.model.Item;
import org.example.usersservice.repository.CategoryRepository;
import org.example.usersservice.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public List<Item> findAllItems() {
        return itemRepository.findAllItems();
    }

    public Item findItemById(Long id) {
        return itemRepository.findItemById(id);
    }

    public CreateItemResultDto createItem(CreateItemDto item) {
        if (item.getOwnerId() == null) {
            log.error("user doesn't exist");
            return CreateItemResultDto.builder().message("user doesn't exist").build();
        }
        if (item.getDescription() == null) {
            itemRepository.createItem(item.getOwnerId(), item.getPrice());
            return CreateItemResultDto.builder().build();
        }
        itemRepository.createItem(item.getOwnerId(), item.getPrice(), item.getDescription());
        return CreateItemResultDto.builder().build();
    }

    public UpdateItemResultDto updateItem(
            Long id, String description, String itemStatus, Double price, Long itemCategory
    ) {
        Item item = itemRepository.findItemById(id);
        Category category = categoryRepository.findCategoryById(itemCategory);
        if (item == null) {
            log.error("item id:'%s' doesn't exist".formatted(id));
            return UpdateItemResultDto.builder().message("item id:'%s' doesn't exist".formatted(id)).build();
        }
        if (category == null) {
            log.info("category id:'%s' doesn't exist".formatted(itemCategory));
            itemRepository.updateItem(description, price, itemStatus.toUpperCase(), id);
            return UpdateItemResultDto.builder().build();
        }
        itemRepository.updateItem(description, price, itemStatus.toUpperCase(), itemCategory, id);
        return UpdateItemResultDto.builder().build();
    }

    public UpdateItemResultDto deleteItem(Long id) {
        Item item = itemRepository.findItemById(id);
        if (item == null) {
            log.error("item id:'%d' doesn't exist".formatted(id));
            return UpdateItemResultDto.builder().message("item id:'%d' doesn't exist".formatted(id)).build();
        }
        itemRepository.deleteItemById(id);
        return UpdateItemResultDto.builder().build();
    }
}
