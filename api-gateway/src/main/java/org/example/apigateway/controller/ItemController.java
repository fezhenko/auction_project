package org.example.apigateway.controller;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.example.apigateway.dto.items.CreateItemDto;
import org.example.apigateway.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private void createItem(
            @RequestBody @Valid CreateItemDto createItemDto
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        itemService.createItem(user, createItemDto);
    }
}
