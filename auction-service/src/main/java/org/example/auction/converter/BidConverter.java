package org.example.auction.converter;

import org.example.auction.dto.bid.BidDto;
import org.example.auction.model.Bid;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BidConverter {
    List<BidDto> toDto(List<Bid> bids);
    BidDto toDto(Bid bid);
}
