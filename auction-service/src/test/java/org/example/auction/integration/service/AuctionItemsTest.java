package org.example.auction.integration.service;

import lombok.AllArgsConstructor;
import org.example.auction.dto.auction.CreateAuctionDto;
import org.example.auction.model.Auction;
import org.example.auction.service.AuctionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AllArgsConstructor
public class AuctionItemsTest {
    private final AuctionService auctionService;
    private CreateAuctionDto createAuctionBySellerId;

    @BeforeEach()
    public void createNewAuction() {
        //create an auction
        createAuctionBySellerId = CreateAuctionDto.builder().sellerId(777_777L).build();
    }

    @AfterEach
    public void deleteTestAuctions() {
        auctionService.deleteAuctionBySellerId(createAuctionBySellerId.getSellerId());
    }

    @Test
    @DisplayName("check auction can be created by seller id")
    public void testCreateAuctionHappyPath() {
        //test creating auction
        auctionService.createAuction(createAuctionBySellerId);
        List<Auction> auctions = auctionService.findAuctionsBySellerId(createAuctionBySellerId.getSellerId());
        //verify the creation
        auctions.forEach(auction -> {
            assertNotNull(auction);
            assertNotNull(auction.getAuctionId());
            assertEquals("PLANNED", auction.getAuctionState());
            assertEquals(0, auction.getStartPrice());
            assertEquals(0, auction.getCurrentPrice());
            assertEquals(0, auction.getMinimalBid());
            assertFalse(auction.getIsPayed());
        });
    }
}
