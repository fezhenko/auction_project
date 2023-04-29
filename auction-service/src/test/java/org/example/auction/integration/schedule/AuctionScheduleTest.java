package org.example.auction.integration.schedule;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;

import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.dto.auction.AuctionItemDto;
import org.example.auction.dto.auction.PriceDto;
import org.example.auction.model.Seller;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.BuyerRepository;
import org.example.auction.repository.SellerRepository;
import org.example.auction.scheduler.UpdateBalanceAfterAuctionFinishSchedule;
import org.example.auction.scheduler.client.UserClient;
import org.example.auction.scheduler.client.dto.AppUserDto;
import org.example.auction.scheduler.client.dto.FinalPriceDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuctionScheduleTest {

    @MockBean
    @Autowired
    AuctionRepository auctionRepository;

    @MockBean
    @Autowired
    SellerRepository sellerRepository;
    @MockBean
    @Autowired
    BuyerRepository buyerRepository;

    @MockBean
    @Autowired
    AuctionConverter auctionConverter;

    @Autowired
    @MockBean
    UserClient userClient;

    @Autowired
    UpdateBalanceAfterAuctionFinishSchedule updateBalanceAfterAuctionFinishSchedule;

    @Test
    public void sellerBalanceShouldBeUpdatedAfterAuctionFinish() {
        AuctionDto auctionDto = AuctionDto.builder()
            .auctionId(123L)
            .auctionDate(Date.valueOf(LocalDate.now(Clock.systemUTC())))
            .auctionState("FINISHED")
            .itemDto(
                AuctionItemDto.builder()
                    .id(89123L)
                    .seller(123L)
                    .build()
            )
            .priceDto(
                PriceDto.builder()
                    .buyer(4412L)
                    .currentPrice(111.11)
                    .startPrice(11.11)
                    .finalPrice(111.11)
                    .minimalBid(5.0)
                    .build()
            )
            .isPayed(false)
            .createdAt(Date.valueOf(LocalDate.now(Clock.systemUTC())))
            .build();
        Seller seller = Seller.builder()
            .sellerId(123L)
            .auctionId(123L)
            .createdAt(Date.valueOf(LocalDate.now(Clock.systemUTC()).minusMonths(12)))
            .email("test@user.com")
            .build();
        AppUserDto appUserDto = AppUserDto.builder()
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

        FinalPriceDto finalPrice =
            FinalPriceDto.builder().finalPrice(auctionDto.getPriceDto().getFinalPrice()).build();

        Mockito.doReturn(seller).when(sellerRepository).findSellerByAuctionId(auctionDto.getAuctionId());
        Mockito.doReturn(appUserDto).when(userClient).findUserByEmail(seller.getEmail());
        Mockito.doNothing().when(userClient).updateUserBalance(appUserDto.getId(), "SELLER", finalPrice);

        updateBalanceAfterAuctionFinishSchedule.updateSellerBalanceAfterAuctionFinish(auctionDto);

        Mockito.verify(userClient, Mockito.times(1)).updateUserBalance(appUserDto.getId(), "SELLER", finalPrice);
    }
}
