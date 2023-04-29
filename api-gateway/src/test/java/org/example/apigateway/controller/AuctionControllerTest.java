package org.example.apigateway.controller;

import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.service.AuctionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class AuctionControllerTest extends ControllersAbstractConfiguration {

    @Autowired
    AuctionService auctionService;

    @Test
    void shouldReturnListWithAuctions() {
        List<AuctionDto> auctions = auctionService.findAllAuction();
        assertNotNull(auctions);
    }
}
