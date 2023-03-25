package org.example.apigateway.client;

import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@FeignClient(name = "auction-service", url = "${services.auction-service.url}/api/v1/buyers")
public interface BuyerClient {
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void createBuyer(@RequestBody @Valid CreateBuyerWithUserEmailDto createBuyerWithUserEmailDto);
}
