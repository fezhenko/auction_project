package org.example.apigateway.service;

import org.example.apigateway.client.AuctionsClient;
import org.example.apigateway.client.ItemClient;
import org.example.apigateway.client.SellerClient;
import org.example.apigateway.client.dto.SellerDto;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.auction.AuctionItemDto;
import org.example.apigateway.dto.auction.PriceDto;
import org.example.apigateway.dto.items.AddItemDto;
import org.example.apigateway.dto.items.ItemDto;
import org.example.apigateway.dto.seller.CreateSellerDto;
import org.example.apigateway.exceptions.CreateAuctionException;
import org.example.apigateway.exceptions.UserEmailIsNullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuctionServiceTest {

    @Mock
    private AuctionsClient auctionsClient;

    @Mock
    private SellerClient sellerClient;

    @Mock
    private ItemClient itemClient;

    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auctionService = new AuctionService(auctionsClient, sellerClient, itemClient);
    }

    @Test
    void testCreateAuction() throws CreateAuctionException, UserEmailIsNullException {
        User user = new User("test@example.com", "", Collections.emptyList());
        CreateSellerDto createSellerRequest = CreateSellerDto.builder().email(user.getUsername()).build();
        SellerDto sellerDto = SellerDto.builder().sellerId(1L).build();

        when(sellerClient.createNewSeller(createSellerRequest)).thenReturn(sellerDto);

        AuctionDto createdAuction = AuctionDto.builder()
                .auctionId(1L)
                .auctionDate(new Date())
                .auctionState("active")
                .itemDto(AuctionItemDto.builder().build())
                .priceDto(PriceDto.builder().build())
                .isPayed(false)
                .createdAt(new Date())
                .build();
        when(auctionsClient.createAuction(sellerDto.getSellerId())).thenReturn(createdAuction);

        auctionService.createAuction(user);

        verify(sellerClient).createNewSeller(createSellerRequest);
        verify(auctionsClient).createAuction(sellerDto.getSellerId());
    }

    @Test
    void testAddItemToAuction() {
        User user = new User("test@example.com", "", Collections.emptyList());
        Long auctionId = 1L;
        AddItemDto addItemDto = AddItemDto.builder().itemId(2L).build();
        ItemDto itemDto = ItemDto.builder().id(2L).price(100.0).build();


        when(itemClient.findItem(addItemDto.getItemId())).thenReturn(itemDto);
        auctionService.addItemToAuction(user, auctionId, addItemDto);

        verify(itemClient).findItem(addItemDto.getItemId());
        verify(auctionsClient).addItemToAuction(eq(auctionId), any());
    }

    @Test
    void testFindAllAuction() {
        List<AuctionDto> auctionList = Collections.singletonList(AuctionDto.builder().build());
        when(auctionsClient.getAllAuctions()).thenReturn(auctionList);

        List<AuctionDto> result = auctionService.findAllAuction();

        verify(auctionsClient).getAllAuctions();
        assertEquals(auctionList, result);
    }
}
