package org.example.auction.converter;

import java.util.List;

import org.example.auction.dto.seller.SellerDto;
import org.example.auction.model.Seller;
import org.mapstruct.Mapper;

@Mapper
public interface SellerConverter {
    List<SellerDto> toDto(List<Seller> sellers);
    SellerDto toDto(Seller seller);
}
