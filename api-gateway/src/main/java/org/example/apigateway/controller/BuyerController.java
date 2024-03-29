package org.example.apigateway.controller;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apigateway.converter.BuyerConverter;
import org.example.apigateway.dto.bid.CreateBidDto;
import org.example.apigateway.dto.buyer.CreateBuyerDto;
import org.example.apigateway.service.BidService;
import org.example.apigateway.service.BuyerService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/buyers")
@AllArgsConstructor
@SecurityScheme(type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
@Slf4j
public class BuyerController {

    private final BuyerService buyerService;
    private final BidService bidService;
    private final BuyerConverter buyerConverter;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private void enrollToAuction(
            @RequestBody @Valid CreateBuyerDto createBuyerDto
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        buyerService.createBuyer(
            buyerConverter.toCreateBuyerWithUserEmailDto(createBuyerDto, user)
        );
    }

    @PostMapping("/bids")
    @ResponseStatus(HttpStatus.ACCEPTED)
    private void makeBidToAuction(
            @RequestBody @Valid CreateBidDto createBidDto
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bidService.makeBidToAuction(user.getUsername(),
                createBidDto.getAuctionId(), createBidDto.getAmount()
        );
    }
}
