package org.example.auction.scheduler.client;

import org.example.auction.scheduler.client.dto.UpdateItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "items-service", url = "${services.users-service.url}/api/v1/items")
public interface ItemClient {

    @RequestMapping(value = "/{itemId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    void updateItemStatus(@PathVariable("itemId") Long userId, UpdateItemDto updateItemDto);
}
