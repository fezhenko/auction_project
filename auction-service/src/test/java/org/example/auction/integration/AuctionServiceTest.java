package org.example.auction.integration;

import lombok.AllArgsConstructor;
import org.example.auction.dto.auction.AddItemToAuctionDto;
import org.example.auction.dto.auction.AuctionResultDto;
import org.example.auction.dto.seller.CreateSellerDto;
import org.example.auction.model.Auction;
import org.example.auction.service.AuctionService;
import org.example.auction.service.SellerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AllArgsConstructor
public class AuctionServiceTest {

    @Autowired
    private SellerService sellerService;
    private final AuctionService auctionService;
    private Auction auction;

    @BeforeEach
    public void createSellerAndAuction() {
        CreateSellerDto sellerDto = CreateSellerDto.builder().email("testSeller123123@gmail.com").build();
        sellerService.createSeller(sellerDto);
        auction = auctionService.findAuctionBySellerEmail("testSeller123123@gmail.com");
    }

    @Test
    public void testItemAddedToAuctionSuccessfully() {
        //given
        AddItemToAuctionDto item =
            AddItemToAuctionDto.builder().itemId(77L).price(1_000_000D).email("testSeller123123@gmail.com").build();
        //when
        AuctionResultDto result = auctionService.addItemToAuction(auction.getAuctionId(), item);
        auction = auctionService.findAuctionBySellerEmail("testSeller123123@gmail.com");
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(item.getItemId(), auction.getItemId());
        Assertions.assertEquals(item.getPrice(), auction.getStartPrice());
        Assertions.assertEquals(item.getPrice() * 0.05, auction.getMinimalBid());
    }

    @AfterEach
    public void deleteSellerAndAuction() {
        auctionService.deleteAuction(auction.getAuctionId());
    }
}
