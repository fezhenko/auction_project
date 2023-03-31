package org.example.apigateway.client;

import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.auction.BuyerEmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "auction-client", url = "${services.auction-service.url}/api/v1/auctions")
public interface AuctionsClient {
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<AuctionDto> getAllAuctions();

    @RequestMapping(method = RequestMethod.GET, value = "/{auctionId}/buyer",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    BuyerEmailDto getBuyerEmailByAuctionId(@PathVariable("auctionId") Long auctionId);

    @RequestMapping(method = RequestMethod.PATCH, value = "/{auctionId}/pay")
    void setIsPayedToTrue(@PathVariable("auctionId") Long auctionId);
}
