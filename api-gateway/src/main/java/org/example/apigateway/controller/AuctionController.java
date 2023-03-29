package org.example.apigateway.controller;

import lombok.AllArgsConstructor;
import org.example.apigateway.dto.auction.CreateAuctionResultDto;
import org.example.apigateway.dto.seller.CreateSellerDto;
import org.example.apigateway.service.AuctionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auctions")
@AllArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseEntity<CreateAuctionResultDto> createAuction(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (token.isEmpty()) {
            return ResponseEntity.badRequest().body(CreateAuctionResultDto.builder().message("Invalid token").build());
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CreateAuctionResultDto result = auctionService.createAuction(
                CreateSellerDto.builder().email(user.getUsername()).build()
        );
        if (result.getMessage().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }
}
