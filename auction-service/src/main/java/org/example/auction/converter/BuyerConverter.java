package org.example.auction.converter;

import org.example.auction.dto.buyer.BuyerDto;
import org.example.auction.model.Buyer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BuyerConverter {
    List<BuyerDto> toDto(List<Buyer> buyers);

    BuyerDto toDto(Buyer buyer);
}
