package org.example.apigateway.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.SneakyThrows;
import org.example.apigateway.config.jwt.Jwt;
import org.example.apigateway.config.service.AuthService;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.auction.AuctionItemDto;
import org.example.apigateway.dto.auction.PriceDto;
import org.example.apigateway.service.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuctionController.class)
public class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private Jwt jwt;
    @MockBean
    private AuthService authService;
    @MockBean
    private AuctionService auctionService;
    private final Random random = new Random();
    private final List<String> auctionStates = List.of("PLANNED", "IN_PROGRESS", "FINISHED", "CANCELED");
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "test@username.com", password = "testPassword", roles = "ADMIN")
    public void auctions_should_be_returned() {
        // given
        List<AuctionDto> expectedAuctions = Arrays.asList(
            generateAuctionDto(), generateAuctionDto(), generateAuctionDto());
        Mockito.doReturn(expectedAuctions).when(auctionService).findAllAuction();

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auctions"));

        // then
        result
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Deserialize the response JSON into a list of AuctionDto
        List<AuctionDto> actualAuctions = objectMapper.readValue(
            result.andReturn().getResponse().getContentAsString(), new TypeReference<>() {}
        );

        assertEquals(expectedAuctions.size(), actualAuctions.size());
        assertTrue(expectedAuctions.containsAll(actualAuctions));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void should_return_ok_when_auctions_are_empty() {
        // given
        List<AuctionDto> expectedAuctions = List.of();
        Mockito.doReturn(expectedAuctions).when(auctionService).findAllAuction();

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auctions"));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void should_throw_exception_if_error() {
        // given
        doThrow(IllegalArgumentException.class).when(auctionService).findAllAuction();

        // then
        assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/api/v1/auctions")));
    }

    // TODO: cover other methods with tests;

    private AuctionDto generateAuctionDto() {
        return AuctionDto.builder()
            .auctionId(random.nextLong())
            .auctionDate(new Date())
            .auctionState(auctionStates.get(random.nextInt(3)))
            .itemDto(generateItemDto())
            .priceDto(generatePriceDto())
            .isPayed(random.nextBoolean())
            .createdAt(new Date())
            .build();
    }

    private AuctionItemDto generateItemDto() {
        return AuctionItemDto.builder()
            .id(random.nextLong())
            .description("item description")
            .seller(random.nextLong())
            .build();
    }

    private PriceDto generatePriceDto() {
        return PriceDto.builder()
            .startPrice(random.nextDouble())
            .minimalBid(random.nextDouble())
            .currentPrice(random.nextDouble())
            .finalPrice(random.nextDouble())
            .buyer(random.nextLong())
            .build();
    }

}
