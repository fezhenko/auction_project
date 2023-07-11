package org.example.apigateway.converter;

import org.example.apigateway.dto.buyer.CreateBuyerDto;
import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.mapstruct.Mapper;
import org.springframework.security.core.userdetails.User;

@Mapper
public interface BuyerConverter {
    CreateBuyerWithUserEmailDto toCreateBuyerWithUserEmailDto(CreateBuyerDto buyerDto, User user);
}
