package org.example.auction.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.auction.converter.ItemConverter;
import org.example.auction.dto.item.CreateItemDto;
import org.example.auction.dto.item.ItemDto;
import org.example.auction.dto.item.UpdateItemDto;
import org.example.auction.dto.item.UpdateItemResultDto;
import org.example.auction.model.Item;
import org.example.auction.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "Items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemConverter itemConverter;

    @GetMapping
    @SneakyThrows
    private ResponseEntity<List<ItemDto>> findAll() {
        List<Item> items = itemService.findAllItems();
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(itemConverter.toDto(items));
    }

    @GetMapping("/{itemId}")
    private ResponseEntity<ItemDto> findAuctionById(@PathVariable("itemId") Long id) {
        Item item = itemService.findItemById(id);
        if (item == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(itemConverter.toDto(item));
    }

    @PostMapping
    private ResponseEntity<UpdateItemResultDto> createItem(
            @RequestBody @Valid CreateItemDto createItemDto
    ) {
        UpdateItemResultDto result = itemService.createItem(
                createItemDto.getAuctionId(), createItemDto.getPrice(), createItemDto.getDescription());
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PatchMapping("/{itemId}")
    private ResponseEntity<UpdateItemResultDto> updateItem(
            @PathVariable("itemId") Long id,
            @RequestBody @Valid UpdateItemDto updateItemDto
    ) {
        UpdateItemResultDto result = itemService.updateItem(
                id, updateItemDto.getItemStatus(), updateItemDto.getItemCategory(), updateItemDto.getPrice(),
                updateItemDto.getDescription()
        );
        if (result.getMessage() == null) {
            ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }


}
