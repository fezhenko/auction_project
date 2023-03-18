package org.example.auction.converter;

import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.model.Auction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AuctionConverter {
    List<AuctionDto> toDto(List<Auction> auctions);
}
