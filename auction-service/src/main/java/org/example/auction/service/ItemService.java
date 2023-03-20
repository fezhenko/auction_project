package org.example.auction.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.dto.item.UpdateItemResultDto;
import org.example.auction.model.Auction;
import org.example.auction.model.Category;
import org.example.auction.model.Item;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.CategoryRepository;
import org.example.auction.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;

    public List<Item> findAllItems() {
        return itemRepository.findAllItems();
    }

    public Item findItemById(Long id) {
        return itemRepository.findItemById(id);
    }

    public UpdateItemResultDto createItem(Long auctionId, Double price, String description) {
        Auction auction = auctionRepository.findAuctionById(auctionId);
        if (auction == null) {
            log.error("auction id:'%s' doesn't exist".formatted(auctionId));
            return UpdateItemResultDto.builder().message("auction id:'%s' doesn't exist".formatted(auctionId)).build();
        }
        if (description != null) {
            itemRepository.createItem(auctionId, price, description);
            return UpdateItemResultDto.builder().build();
        }
        itemRepository.createItem(auctionId, price);
        return UpdateItemResultDto.builder().build();
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
            log.error("item id:'%s' doesn't exist".formatted(id));
            return UpdateItemResultDto.builder().message("item id:'%s' doesn't exist".formatted(id)).build();
        }
        itemRepository.deleteItemByAuctionId(id);
        return UpdateItemResultDto.builder().build();
    }
}
