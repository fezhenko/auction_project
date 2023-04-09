package org.example.usersservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.usersservice.converter.ItemConverter;
import org.example.usersservice.dto.item.CreateItemDto;
import org.example.usersservice.dto.item.CreateItemResultDto;
import org.example.usersservice.dto.item.ItemDto;
import org.example.usersservice.dto.item.UpdateItemDto;
import org.example.usersservice.dto.item.UpdateItemResultDto;
import org.example.usersservice.model.Item;
import org.example.usersservice.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class ItemsController {
    private final ItemService itemService;
    private final ItemConverter itemConverter;

    @Operation(summary = "find all items")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "list of items"),
                    @ApiResponse(responseCode = "204", description = "no content")
            })
    @GetMapping
    @SneakyThrows
    private ResponseEntity<List<ItemDto>> findAll() {
        List<Item> items = itemService.findAllItems();
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(itemConverter.toDto(items));
    }
    @Operation(summary = "find item by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "item is found"),
                    @ApiResponse(responseCode = "204", description = "no content")
            })
    @GetMapping("/{itemId}")
    @SneakyThrows
    private ResponseEntity<ItemDto> findAuctionById(@PathVariable("itemId") Long id) {
        Item item = itemService.findItemById(id);
        if (item == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(itemConverter.toDto(item));
    }
    @Operation(summary = "create new item")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "item is created"),
                    @ApiResponse(responseCode = "400", description = "invalid body")
            })
    @PostMapping
    private ResponseEntity<CreateItemResultDto> createItem(
            @RequestBody @Valid CreateItemDto createItemDto
    ) {
        CreateItemResultDto result = itemService.createItem(createItemDto);
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }
    @Operation(summary = "update item by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "item is updated"),
                    @ApiResponse(responseCode = "204", description = "no content")
            })
    @PatchMapping("/{itemId}")
    private ResponseEntity<UpdateItemResultDto> updateItem(
            @PathVariable("itemId") Long id,
            @RequestBody @Valid UpdateItemDto updateItemDto
    ) {
        UpdateItemResultDto result = itemService.updateItem(
                id, updateItemDto.getDescription(), updateItemDto.getItemStatus(), updateItemDto.getPrice(),
                updateItemDto.getItemCategory()
        );
        if (result.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }
    @Operation(summary = "delete item by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "item is deleted"),
                    @ApiResponse(responseCode = "204", description = "no content")
            })
    @DeleteMapping("/{itemId}")
    private ResponseEntity<UpdateItemResultDto> deleteItem(@PathVariable("itemId") Long id) {
        UpdateItemResultDto result = itemService.deleteItem(id);
        if (result.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }

}
