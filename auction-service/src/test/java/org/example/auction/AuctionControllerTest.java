package org.example.auction;

import org.example.auction.model.Auction;
import org.example.auction.service.AuctionService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class AuctionControllerTest {
    @Mock
    AuctionService auctionService;

    public void setupMocks() {
        Mockito.when(auctionService.findAuctionById(1L))
                .thenReturn(
                        Auction.builder().auctionId(1L).auctionDate(new Date(111111111111111L)).auctionState("PLANNED")
                                .sellerId(1L).itemId(2L).itemDescription("mock description").startPrice(10000d)
                                .currentPrice(25000d).finalPrice(0d).buyerId(7L)
                                .createdAt(new Date(111000000111111L)).minimalBid(25000d*0.05)
                                .lastUpdated(new Date(111111111111112L)).isPayed(false)
                                .build());
    }
}
