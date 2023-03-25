package org.example.apigateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apigateway.client.BidsClient;
import org.example.apigateway.client.UserClient;
import org.example.apigateway.converter.UserConverter;
import org.example.apigateway.dto.UserDto;
import org.example.apigateway.dto.bid.CreateBidDtoWithUserEmail;
import org.example.apigateway.dto.bid.MakeBidResultDto;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BidService {
    private final BidsClient bidsClient;
    private final UserClient userClient;
    private final UserConverter userConverter;

    public MakeBidResultDto makeBidToAuction(String email, Double amount) {
        if (email == null) {
            log.error("user name should be not null");
            MakeBidResultDto.builder().message("user name should be not null").build();
        }
        //get User by username
        UserDto user = userConverter.toDto(userClient.findUserByEmail(email));
        Double userBalance = user.getBalance();
        //check bid amount less than balance
        if (userBalance >= amount) {
            CreateBidDtoWithUserEmail bid = CreateBidDtoWithUserEmail.builder().email(email).amount(amount).build();
            bidsClient.makeBidToAuction(bid);
            return MakeBidResultDto.builder().build();
        }
        return MakeBidResultDto.builder()
                .message("user balance: '%s' is not enough to make bid: '%s'".formatted(userBalance, amount))
                .build();
    }

}
