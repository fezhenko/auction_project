package org.example.apigateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apigateway.client.BidsClient;
import org.example.apigateway.client.UserClient;
import org.example.apigateway.converter.UserConverter;
import org.example.apigateway.dto.UserDto;
import org.example.apigateway.dto.bid.CreateBidDtoWithUserEmail;
import org.example.apigateway.exceptions.UserBalanceIsNotEnoughException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BidService {
    private final BidsClient bidsClient;
    private final UserClient userClient;
    private final UserConverter userConverter;

    public void makeBidToAuction(String email, Long auctionId, Double amount) {
        //get User by username
        UserDto user = userConverter.toDto(userClient.findUserByEmail(email));
        Double userBalance = user.getBalance();
        //check bid amount less than balance
        if (userBalance >= amount) {
            CreateBidDtoWithUserEmail bid = CreateBidDtoWithUserEmail.builder()
                .email(email).auctionId(auctionId).amount(amount).build();
            bidsClient.makeBidToAuction(bid);
        }
        throw new UserBalanceIsNotEnoughException("user balance: '%s' is not enough to make bid: '%s'".formatted(userBalance, amount));
    }

}
