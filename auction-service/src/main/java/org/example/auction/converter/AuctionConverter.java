package org.example.auction.converter;

import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.dto.auction.AuctionItemDto;
import org.example.auction.dto.auction.PriceDto;
import org.example.auction.model.Auction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface AuctionConverter {
    @Mapping(source = "List<Auction>", target = "List<AuctionDto>", qualifiedByName = "AuctionListToDto")
    @Named("AuctionListToDto")
    default List<AuctionDto> toDto(List<Auction> auctions) {
        List<AuctionDto> auctionsDtoList = new ArrayList<>();
        for (Auction auction : auctions) {
            AuctionDto auctionDto = AuctionDto.builder()
                    .auctionId(auction.getAuctionId())
                    .auctionDate(auction.getAuctionDate())
                    .priceDto(
                            PriceDto.builder()
                                    .startPrice(auction.getStartPrice())
                                    .currentPrice(auction.getCurrentPrice())
                                    .finalPrice(auction.getFinalPrice())
                                    .minimalBid(auction.getMinimalBid())
                                    .build()
                    )
                    .itemDto(
                            AuctionItemDto.builder()
                                    .id(auction.getItemId())
                                    .description(auction.getItemDescription())
                                    .build()
                    )
                    .auctionState(auction.getAuctionState())
                    .createdAt(auction.getCreatedAt())
                    .build();
            auctionsDtoList.add(auctionDto);
        }
        return auctionsDtoList;
    }

    @Mapping(source = "Auction", target = "AuctionDto", qualifiedByName = "AuctionToDto")
    @Named("AuctionToDto")
    default AuctionDto toDto(Auction auction) {
        return AuctionDto.builder()
                .auctionId(auction.getAuctionId())
                .auctionDate(auction.getAuctionDate())
                .priceDto(
                        PriceDto.builder()
                                .startPrice(auction.getStartPrice())
                                .currentPrice(auction.getCurrentPrice())
                                .finalPrice(auction.getFinalPrice())
                                .minimalBid(auction.getMinimalBid())
                                .build()
                )
                .itemDto(
                        AuctionItemDto.builder()
                                .id(auction.getItemId())
                                .description(auction.getItemDescription())
                                .build()
                )
                .auctionState(auction.getAuctionState())
                .createdAt(auction.getCreatedAt())
                .build();
    }

}
