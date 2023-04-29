package org.example.auction.converter;

import org.example.auction.dto.buyer.BuyerDto;
import org.example.auction.model.Buyer;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface BuyerConverter {
    List<BuyerDto> toDto(List<Buyer> buyers);

    BuyerDto toDto(Buyer buyer);
}
