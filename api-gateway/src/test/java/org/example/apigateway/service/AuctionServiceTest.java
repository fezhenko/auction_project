package org.example.apigateway.service;

import java.util.List;

import org.example.apigateway.dto.auction.AuctionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuctionServiceTest extends AbstractConfiguration {

    @Autowired
    AuctionService auctionService;

    @Test
    void shouldReturnListWithAuctions() {
        List<AuctionDto> auctions = auctionService.findAllAuction();
        assertNotNull(auctions);
    }
}
