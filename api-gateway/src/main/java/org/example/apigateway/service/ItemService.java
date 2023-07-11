package org.example.apigateway.service;

import lombok.RequiredArgsConstructor;
import org.example.apigateway.client.ItemClient;
import org.example.apigateway.client.UserClient;
import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.dto.items.CreateItemAdjustedDto;
import org.example.apigateway.dto.items.CreateItemDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemClient itemClient;
    private final UserClient userClient;

    public void createItem(User user, CreateItemDto createItemDto) {
        AppUserDto appUser = userClient.findUserByEmail(user.getUsername());
        itemClient.createItem(
                CreateItemAdjustedDto.builder()
                        .ownerId(appUser.getId())
                        .price(createItemDto.getPrice())
                        .description(createItemDto.getDescription())
                        .build()
        );
    }
}
