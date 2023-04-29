package org.example.auction.unit.auction;

import lombok.SneakyThrows;
import org.example.auction.controller.AuctionController;
import org.example.auction.converter.AuctionConverter;
import org.example.auction.service.AuctionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest({AuctionController.class, AuctionConverter.class})
public class AuctionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuctionService auctionService;
    @MockBean
    private AuctionConverter auctionConverter;

    @Test
    @SneakyThrows
    public void shouldGetAuctionByIdHappyPath() {
        final Long auctionId = 123123L;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/auctions/{auctionId}", auctionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent());
        BDDMockito.then(auctionService)
                .should()
                .findAuctionById(auctionId);
    }
}

//Auction.builder()
//        .auctionId(222L)
//        .auctionDate(new Date(111111111111111L))
//        .auctionState("PLANNED")
//        .sellerId(123L)
//        .itemId(2L)
//        .itemDescription("mock description")
//        .startPrice(10000d)
//        .currentPrice(25000d)
//        .finalPrice(0d)
//        .buyerId(7L)
//        .createdAt(new Date(111000000111111L))
//        .minimalBid(25000d * 0.05)
//        .lastUpdated(new Date(111111111111112L))
//        .isPayed(false)
//        .build()
