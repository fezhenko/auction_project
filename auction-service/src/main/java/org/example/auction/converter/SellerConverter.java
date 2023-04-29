package org.example.auction.converter;

import org.example.auction.dto.seller.SellerDto;
import org.example.auction.model.Seller;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SellerConverter {
    List<SellerDto> toDto(List<Seller> sellers);
    SellerDto toDto(Seller seller);
}
