package org.example.apigateway.controller;

import java.util.List;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.items.AddItemDto;
import org.example.apigateway.dto.items.ItemResultDto;
import org.example.apigateway.dto.seller.CreateSellerResultDto;
import org.example.apigateway.service.AuctionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auctions")
@AllArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping
    private ResponseEntity<List<AuctionDto>> findAllAuctions(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (token.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<AuctionDto> auctions = auctionService.findAllAuction();
        if (auctions.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(auctions);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseEntity<CreateSellerResultDto> createAuction(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (token.isEmpty()) {
            return ResponseEntity.badRequest().body(CreateSellerResultDto.builder().message("Invalid token").build());
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CreateSellerResultDto result = auctionService.createAuction(user);
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/{auctionId}/item")
    @ResponseStatus(HttpStatus.OK)
    private ResponseEntity<ItemResultDto> addItemToAuction(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
        @PathVariable("auctionId") Long auctionId,
        @RequestBody @Valid AddItemDto item) {
        if (token.isEmpty()) {
            ItemResultDto result = ItemResultDto.builder().message("Invalid token").build();
            return ResponseEntity.badRequest().body(result);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ItemResultDto result = auctionService.addItemToAuction(user, auctionId, item);
        if (result == null) {
            return ResponseEntity.ok().build();
        }
        return null;
    }
}
