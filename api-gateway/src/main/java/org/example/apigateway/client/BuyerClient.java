package org.example.apigateway.client;

import jakarta.validation.Valid;
import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auction-service", url = "${services.auction-service.url}/api/v1/buyers")
public interface BuyerClient {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    void createBuyer(@RequestBody @Valid CreateBuyerWithUserEmailDto createBuyerWithUserEmailDto);
}
