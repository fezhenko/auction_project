package org.example.auction.converter;

import java.util.List;

import org.example.auction.dto.bid.BidDto;
import org.example.auction.model.Bid;
import org.mapstruct.Mapper;

@Mapper
public interface BidConverter {
    List<BidDto> toDto(List<Bid> bids);
    BidDto toDto(Bid bid);
}
