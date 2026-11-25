package org.example.auction.scheduler.client;

import org.example.auction.scheduler.client.dto.UpdateItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "items-service", url = "${services.users-service.url}/api/v1/items")
public interface ItemClient {

    @PatchMapping(
            value = "/{itemId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updateItemStatus(@PathVariable("itemId") Long userId, UpdateItemDto updateItemDto);
}
