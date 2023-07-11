package org.example.apigateway.client;


import java.util.List;

import org.example.apigateway.client.dto.BuyerDto;
import org.example.apigateway.client.dto.SellerDto;
import org.example.apigateway.dto.auction.AddItemToAuctionDto;
import org.example.apigateway.dto.auction.AuctionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "auction-client", url = "${services.auction-service.url}/api/v1/auctions")
public interface AuctionsClient {
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<AuctionDto> getAllAuctions();

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    AuctionDto createAuction(Long sellerId);

    @RequestMapping(method = RequestMethod.GET, value = "/{auctionId}/buyer",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    BuyerDto getBuyerByAuctionId(@PathVariable("auctionId") Long auctionId);

    @RequestMapping(method = RequestMethod.GET, value = "/{auctionId}/seller",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    SellerDto getSellerByAuctionId(@PathVariable("auctionId") Long auctionId);

    @RequestMapping(method = RequestMethod.PATCH, value = "/{auctionId}/pay")
    void setPayed(@PathVariable("auctionId") Long auctionId);

    @RequestMapping(method = RequestMethod.POST, value = "/{auctionId}/item")
    void addItemToAuction(@PathVariable("auctionId") Long auctionId, AddItemToAuctionDto itemToAuction);
}
