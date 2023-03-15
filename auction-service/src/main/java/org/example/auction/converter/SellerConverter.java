package org.example.auction.converter;

import org.example.auction.dto.seller.SellerDto;
import org.example.auction.model.Seller;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SellerConverter {
    List<SellerDto> toDto(List<Seller> sellers);
    SellerDto toDto(Seller seller);

}
