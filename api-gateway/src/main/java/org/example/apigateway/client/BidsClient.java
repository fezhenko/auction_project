package org.example.apigateway.client;

import org.example.apigateway.dto.bid.CreateBidDtoWithUserEmail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "bids-service", url = "${services.auction-service.url}/api/v1/bids")
public interface BidsClient {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    void makeBidToAuction(CreateBidDtoWithUserEmail createBidDtoWithUserEmail);
}
