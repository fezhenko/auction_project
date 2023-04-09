package org.example.apigateway.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.apigateway.dto.items.CreateItemDto;
import org.example.apigateway.dto.items.CreateItemResultDto;
import org.example.apigateway.service.ItemService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    private ResponseEntity<CreateItemResultDto> createItem(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                           @RequestBody @Valid CreateItemDto createItemDto) {
        if (token.isEmpty()) {
            CreateItemResultDto result = CreateItemResultDto.builder().message("Invalid token").build();
            return ResponseEntity.badRequest().body(result);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CreateItemResultDto result = itemService.createItem(user, createItemDto);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }
}
