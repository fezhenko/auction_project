package org.example.apigateway.controller;

import java.util.List;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.items.AddItemDto;
import org.example.apigateway.service.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auctions")
@AllArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping
    private ResponseEntity<List<AuctionDto>> findAllAuctions() {
        List<AuctionDto> auctions = auctionService.findAllAuction();
        if (auctions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(auctions);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SneakyThrows
    private ResponseEntity<AuctionDto> createAuction() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.createAuction(user));
    }

    @PostMapping("/{auctionId}/item")
    @ResponseStatus(HttpStatus.OK)
    private void addItemToAuction(
            @PathVariable("auctionId") Long auctionId, @RequestBody @Valid AddItemDto item
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auctionService.addItemToAuction(user, auctionId, item);
    }
}
