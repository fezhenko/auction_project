package org.example.usersservice.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usersservice.dto.item.CreateItemDto;
import org.example.usersservice.exceptions.ItemIsNullException;
import org.example.usersservice.exceptions.UserIdIsNullException;
import org.example.usersservice.model.Category;
import org.example.usersservice.model.Item;
import org.example.usersservice.repository.CategoryRepository;
import org.example.usersservice.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public List<Item> findAllItems() {
        try {
            return itemRepository.findAllItems();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    public Item findItemById(Long id) {
        try {
            return itemRepository.findItemById(id);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    public void createItem(CreateItemDto item) {
        if (item.getOwnerId() == null) {
            log.error("user id cannot be null");
            throw new UserIdIsNullException("user id cannot be null");
        }
        if (item.getDescription() == null) {
            itemRepository.createItem(item.getOwnerId(), item.getPrice());
            return;
        }
        itemRepository.createItem(item.getOwnerId(), item.getPrice(), item.getDescription());
    }

    public void updateItem(
            Long id, String description, String itemStatus, Double price, Long itemCategory
    ) {
        Item item = itemRepository.findItemById(id);
        Category category = categoryRepository.findCategoryById(itemCategory);
        if (item == null) {
            log.error("item id:'%s' doesn't exist".formatted(id));
            throw new ItemIsNullException("item id:'%s' doesn't exist".formatted(id));
        }
        if (category == null) {
            log.info("category id:'%s' doesn't exist".formatted(itemCategory));
            itemRepository.updateItem(description, price, itemStatus.toUpperCase(), id);
            return;
        }
        itemRepository.updateItem(description, price, itemStatus.toUpperCase(), itemCategory, id);
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findItemById(id);
        if (item == null) {
            log.error("item id:'%d' doesn't exist".formatted(id));
            throw new ItemIsNullException("item id:'%s' doesn't exist".formatted(id));
        }
        itemRepository.deleteItemById(id);
    }
}
