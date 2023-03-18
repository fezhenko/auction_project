package org.example.auction.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.model.Auction;
import org.example.auction.service.AuctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auctions")
@Tag(name = "Auctions")
@AllArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionConverter auctionConverter;

    @GetMapping
    private ResponseEntity<List<AuctionDto>> findAuctions() {
        List<Auction> auctions = auctionService.findAllAuctions();
        return ResponseEntity.ok(auctionConverter.toDto(auctions));
    }
}
