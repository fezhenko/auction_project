package org.example.auction.unit.converter;

import java.util.List;

import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.model.Auction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.example.auction.AuctionUtil.generateAuction;

public class AuctionConverterTest {

    @Test
    public void should_convert_auction_to_buyer_with_email_dto() {
        // given
        AuctionConverter auctionConverter = new AuctionConverter() {
            @Override
            public List<AuctionDto> toDto(List<Auction> auctions) {
                return AuctionConverter.super.toDto(auctions);
            }

            @Override
            public AuctionDto toDto(Auction auction) {
                return AuctionConverter.super.toDto(auction);
            }
        };

        List<Auction> auctions = List.of(generateAuction());

        // when
        List<AuctionDto> actualAuctionsDto = auctionConverter.toDto(auctions);

        // then
        Assertions.assertEquals(auctions.get(0).getAuctionId(), actualAuctionsDto.get(0).getAuctionId());
        Assertions.assertEquals(auctions.get(0).getItemId(), actualAuctionsDto.get(0).getItemDto().getId());
        Assertions.assertEquals(auctions.get(0).getFinalPrice(), actualAuctionsDto.get(0).getPriceDto().getFinalPrice());
    }

}
