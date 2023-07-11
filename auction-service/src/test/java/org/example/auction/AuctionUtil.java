package org.example.auction;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.example.auction.dto.auction.AddItemToAuctionDto;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.dto.auction.AuctionItemDto;
import org.example.auction.dto.auction.PriceDto;
import org.example.auction.model.Auction;

public class AuctionUtil {

    private static final Random RANDOM = new Random();
    private static final List<String> AUCTION_STATES = List.of("PLANNED", "IN_PROGRESS", "FINISHED", "CANCELED");

    public static Auction generateAuction() {
        double startPrice = RANDOM.nextDouble();

        return Auction.builder()
            .auctionId(RANDOM.nextLong())
            .auctionDate(new Date())
            .auctionState(AUCTION_STATES.get(RANDOM.nextInt(3)))
            .sellerId(RANDOM.nextLong())
            .itemId(RANDOM.nextLong())
            .startPrice(startPrice)
            .currentPrice(RANDOM.nextDouble())
            .minimalBid(startPrice * 0.05)
            .finalPrice(RANDOM.nextDouble())
            .buyerId(RANDOM.nextLong())
            .createdAt(new Date())
            .lastUpdated(new Date())
            .isPayed(RANDOM.nextBoolean())
            .build();
    }

    public static Auction generateAuctionByItemDto(long auctionId, AddItemToAuctionDto addItemToAuctionDto) {
        return Auction.builder()
            .auctionId(auctionId)
            .auctionDate(new Date())
            .auctionState(AUCTION_STATES.get(RANDOM.nextInt(3)))
            .sellerId(RANDOM.nextLong())
            .itemId(RANDOM.nextLong())
            .startPrice(addItemToAuctionDto.getPrice())
            .currentPrice(RANDOM.nextDouble())
            .minimalBid(addItemToAuctionDto.getPrice() * 0.05)
            .finalPrice(RANDOM.nextDouble())
            .buyerId(RANDOM.nextLong())
            .createdAt(new Date())
            .lastUpdated(new Date())
            .isPayed(RANDOM.nextBoolean())
            .build();
    }

    public static AddItemToAuctionDto generateAddItemToAuctionDto(long itemId, double price, String email) {
        return AddItemToAuctionDto.builder()
            .email(email)
            .itemId(itemId)
            .price(price)
            .build();
    }

    private static AuctionItemDto generateItemDto() {
        return AuctionItemDto.builder()
            .id(RANDOM.nextLong())
            .seller(RANDOM.nextLong())
            .build();
    }

    private static PriceDto generatePriceDto() {
        return PriceDto.builder()
            .startPrice(RANDOM.nextDouble())
            .minimalBid(RANDOM.nextDouble())
            .currentPrice(RANDOM.nextDouble())
            .finalPrice(RANDOM.nextDouble())
            .buyer(RANDOM.nextLong())
            .build();
    }


    public static Auction generateAuctionWithoutItem(long auctionId) {
        return Auction.builder()
            .auctionId(auctionId)
            .auctionDate(new Date())
            .auctionState(AUCTION_STATES.get(RANDOM.nextInt(3)))
            .sellerId(RANDOM.nextLong())
            .itemId(null)
            .startPrice(null)
            .currentPrice(null)
            .minimalBid(null)
            .finalPrice(null)
            .buyerId(null)
            .createdAt(new Date())
            .lastUpdated(new Date())
            .isPayed(false)
            .build();
    }

    public static AuctionDto generateAuctionDto() {
        return AuctionDto.builder()
            .auctionId(123L)
            .auctionDate(java.sql.Date.valueOf(LocalDate.now(Clock.systemUTC())))
            .auctionState("FINISHED")
            .itemDto(generateItemDto())
            .priceDto(generatePriceDto())
            .isPayed(false)
            .createdAt(java.sql.Date.valueOf(LocalDate.now(Clock.systemUTC())))
            .build();
    }

    public static AuctionDto generateAuctionDtoById(long auctionId) {
        return AuctionDto.builder()
            .auctionId(auctionId)
            .auctionDate(new Date())
            .auctionState(AUCTION_STATES.get(RANDOM.nextInt(3)))
            .itemDto(null)
            .priceDto(null)
            .isPayed(false)
            .createdAt(new Date())
            .build();
    }
}
