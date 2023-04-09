package org.example.apigateway.client;

import org.example.apigateway.dto.items.CreateItemAdjustedDto;
import org.example.apigateway.dto.items.CreateItemResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "items-client", url = "${services.users-service.url}/api/v1/items")
public interface ItemClient {
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    CreateItemResultDto createItem(CreateItemAdjustedDto createItemAdjustedDto);
}
