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
            auctionsDtoList.add(toDto(auction));
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
                                .minimalBid(auction.getMinimalBid())
                                .currentPrice(auction.getCurrentPrice())
                                .finalPrice(auction.getFinalPrice())
                                .buyer(auction.getBuyerId())
                                .build()
                )
                .itemDto(
                        AuctionItemDto.builder()
                                .id(auction.getItemId())
                                .description(auction.getItemDescription())
                                .seller(auction.getSellerId())
                                .build()
                )
                .isPayed(auction.getIsPayed())
                .auctionState(auction.getAuctionState())
                .createdAt(auction.getCreatedAt())
                .build();
    }

}
