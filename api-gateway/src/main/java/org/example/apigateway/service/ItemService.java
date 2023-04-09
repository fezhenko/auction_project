package org.example.apigateway.service;

import lombok.RequiredArgsConstructor;
import org.example.apigateway.client.ItemClient;
import org.example.apigateway.client.UserClient;
import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.dto.items.CreateItemAdjustedDto;
import org.example.apigateway.dto.items.CreateItemDto;
import org.example.apigateway.dto.items.ItemResultDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemClient itemClient;
    private final UserClient userClient;

    public ItemResultDto createItem(User user, CreateItemDto createItemDto) {
        if (user.getUsername() == null) {
            return ItemResultDto.builder()
                .message("User with '%s' is not exist".formatted(user.getUsername())).build();
        }
        AppUserDto appUser = userClient.findUserByEmail(user.getUsername());
        CreateItemAdjustedDto item = CreateItemAdjustedDto.builder()
            .ownerId(appUser.getId())
            .price(createItemDto.getPrice())
            .description(createItemDto.getDescription())
            .build();
        return itemClient.createItem(item);
    }
}
