package org.example.auction.converter;

import java.util.List;

import org.example.auction.dto.buyer.BuyerDto;
import org.example.auction.model.Buyer;
import org.mapstruct.Mapper;

@Mapper
public interface BuyerConverter {
    List<BuyerDto> toDto(List<Buyer> buyers);

    BuyerDto toDto(Buyer buyer);
}
