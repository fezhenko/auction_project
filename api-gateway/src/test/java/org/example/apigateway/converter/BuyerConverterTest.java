package org.example.apigateway.converter;

import java.util.List;

import org.example.apigateway.dto.buyer.CreateBuyerDto;
import org.example.apigateway.dto.buyer.CreateBuyerWithUserEmailDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

public class BuyerConverterTest {

    @Test
    public void should_convert_buyer_to_buyer_with_email_dto() {
        // given
        BuyerConverter buyerConverter = new BuyerConverter() {
            @Override
            public CreateBuyerWithUserEmailDto toCreateBuyerWithUserEmailDto(CreateBuyerDto buyerDto, User user) {
                if (buyerDto == null && user == null) {
                    return null;
                }

                CreateBuyerWithUserEmailDto.CreateBuyerWithUserEmailDtoBuilder createBuyerWithUserEmailDto =
                    CreateBuyerWithUserEmailDto.builder();

                if (buyerDto != null) {
                    createBuyerWithUserEmailDto.auctionId(buyerDto.getAuctionId());
                }
                if (user != null) {
                    createBuyerWithUserEmailDto.username(user.getUsername());
                }

                return createBuyerWithUserEmailDto.build();
            }
        };

        User user = new User("username@username.test", "password", List.of());
        CreateBuyerDto createBuyerDto = CreateBuyerDto.builder().auctionId(777L).build();

        // when
        CreateBuyerWithUserEmailDto createBuyerWithUserEmailDto =
            buyerConverter.toCreateBuyerWithUserEmailDto(createBuyerDto, user);

        // then
        Assertions.assertEquals(user.getUsername(), createBuyerWithUserEmailDto.getUsername());
        Assertions.assertEquals(createBuyerDto.getAuctionId(), createBuyerWithUserEmailDto.getAuctionId());
    }
}
