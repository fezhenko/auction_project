package org.example.auction.unit.controller.auction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.auction.AuctionUtil;
import org.example.auction.controller.AuctionController;
import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.model.Auction;
import org.example.auction.service.AuctionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest({AuctionController.class, AuctionConverter.class})
public class AuctionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuctionService auctionService;
    @MockBean
    private AuctionConverter auctionConverter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    public void should_get_auction_by_auction_id() {
        // given
        long auctionId = 123123L;
        Auction auction = AuctionUtil.generateAuctionWithoutItem(auctionId);
        AuctionDto auctionDto = AuctionUtil.generateAuctionDtoById(auctionId);

        doReturn(auction).when(auctionService).findAuctionById(auctionId);
        doReturn(auctionDto).when(auctionConverter).toDto(auction);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auctions/{auctionId}", auctionId));

        // then
        result.andExpect(status().isOk());

        AuctionDto actualResult = objectMapper.readValue(
            result.andReturn().getResponse().getContentAsString(), new TypeReference<>() {}
        );
        Assertions.assertEquals(auction.getAuctionId(), actualResult.getAuctionId());
    }

    // TODO: cover rest of the cases with tests
}
