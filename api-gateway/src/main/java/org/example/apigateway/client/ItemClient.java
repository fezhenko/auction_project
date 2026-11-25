package org.example.apigateway.client;

import org.example.apigateway.dto.items.CreateItemAdjustedDto;
import org.example.apigateway.dto.items.ItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "items-client", url = "${services.users-service.url}/api/v1/items")
public interface ItemClient {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    void createItem(CreateItemAdjustedDto createItemAdjustedDto);

    @GetMapping(
            value = "/{itemId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ItemDto findItem(@PathVariable("itemId") Long itemId);
}
