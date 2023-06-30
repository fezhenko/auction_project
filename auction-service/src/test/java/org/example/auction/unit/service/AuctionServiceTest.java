package org.example.auction.unit.service;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;

import org.example.auction.AuctionUtil;
import org.example.auction.dto.auction.AddItemToAuctionDto;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.exceptions.auction.ItemAlreadyExistException;
import org.example.auction.model.Auction;
import org.example.auction.model.Seller;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.SellerRepository;
import org.example.auction.scheduler.UserType;
import org.example.auction.scheduler.client.UserClient;
import org.example.auction.scheduler.client.dto.AppUserDto;
import org.example.auction.scheduler.client.dto.FinalPriceDto;
import org.example.auction.service.AuctionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.auction.AuctionUtil.generateAddItemToAuctionDto;
import static org.example.auction.AuctionUtil.generateAuctionByItemDto;
import static org.example.auction.AuctionUtil.generateAuctionWithoutItem;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {
    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AuctionService auctionService;

    @Test
    public void should_add_item_to_auction() {
        //given
        long auctionId = 89789L;
        long itemId = 123L;
        double price = 1_000_000;
        Seller seller = generateSeller();
        AddItemToAuctionDto itemToAuctionDto = generateAddItemToAuctionDto(itemId, price, seller.getEmail());
        Auction auctionWithoutItem = generateAuctionWithoutItem(auctionId);

        Mockito.doReturn(auctionWithoutItem).when(auctionRepository).findAuctionByAuctionId(any(Long.class));
        Mockito.doReturn(seller).when(sellerRepository).findSellersBySellerId(any(Long.class));
        Mockito.doNothing().when(auctionRepository)
            .addItemToAuction(any(Long.class), any(Long.class), any(Double.class), any(Double.class));

        //when
        auctionService.addItemToAuction(auctionId, itemToAuctionDto);

        //then
        verify(auctionRepository).addItemToAuction(eq(auctionId), eq(itemId), eq(price), eq(50000.0d));
    }

    @Test
    public void should_return_item_already_exist_exception_if_item_already_exist() {
        //given
        long auctionId = 89789L;
        long itemId = 123L;
        double price = 1_000_000;

        Seller seller = generateSeller();
        AddItemToAuctionDto addItemToAuctionDto = generateAddItemToAuctionDto(itemId, price, seller.getEmail());
        Auction auction = generateAuctionByItemDto(auctionId, addItemToAuctionDto);

        Mockito.doReturn(auction).when(auctionRepository).findAuctionByAuctionId(auctionId);

        //then
        assertThrows(
            ItemAlreadyExistException.class, () -> auctionService.addItemToAuction(auctionId, addItemToAuctionDto));
    }

    @Test
    public void seller_balance_should_be_updated_when_auction_finished() {
        // given
        AppUserDto appUserDto = generateAppUserDto();
        Seller seller = generateSeller();
        AuctionDto auctionDto = AuctionUtil.generateAuctionDto();
        FinalPriceDto finalPrice =
            FinalPriceDto.builder().finalPrice(auctionDto.getPriceDto().getFinalPrice()).build();

        Mockito.doReturn(seller).when(sellerRepository).findSellerByAuctionId(anyLong());
        Mockito.doReturn(appUserDto).when(userClient).findUserByEmail(any(String.class));
        Mockito.doNothing().when(userClient).updateUserBalance(appUserDto.getId(), "SELLER", finalPrice);

        // when
        auctionService.updateSellerBalanceAfterAuctionFinish(auctionDto);

        // then
        verify(userClient, Mockito.times(1))
            .updateUserBalance(eq(appUserDto.getId()), eq(UserType.SELLER.name()), eq(finalPrice));
    }

    private Seller generateSeller() {
        return Seller.builder()
            .sellerId(89789L)
            .auctionId(123L)
            .createdAt(Date.valueOf(LocalDate.now(Clock.systemUTC()).minusMonths(12)))
            .email("test@user.com")
            .build();
    }

    private AppUserDto generateAppUserDto() {
        return AppUserDto.builder()
            .id(2L)
            .balance(1000d)
            .role("USER")
            .email("test@user.com")
            .firstname("test")
            .lastname("test")
            .password("test")
            .createdAt(Date.valueOf(LocalDate.now(Clock.systemUTC()).minusMonths(36)))
            .phoneNumber(null)
            .build();
    }

}
