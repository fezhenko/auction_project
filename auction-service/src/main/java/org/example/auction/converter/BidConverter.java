package org.example.auction.converter;

import org.example.auction.dto.bid.BidDto;
import org.example.auction.model.Bid;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface BidConverter {
    List<BidDto> toDto(List<Bid> bids);
    BidDto toDto(Bid bid);
}
