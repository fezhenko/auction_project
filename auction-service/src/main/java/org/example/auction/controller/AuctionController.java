package org.example.auction.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.AddItemToAuctionDto;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.dto.auction.CreateAuctionDto;
import org.example.auction.dto.auction.CreateAuctionResultDto;
import org.example.auction.dto.auction.UserEmailDto;
import org.example.auction.dto.auction.UpdateAuctionDateDto;
import org.example.auction.dto.auction.UpdateAuctionItemDto;
import org.example.auction.dto.auction.AuctionResultDto;
import org.example.auction.dto.auction.UpdateAuctionPriceDto;
import org.example.auction.dto.auction.UpdateAuctionStateDto;
import org.example.auction.model.Auction;
import org.example.auction.service.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auctions")
@Tag(name = "Auctions")
@AllArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionConverter auctionConverter;

    @GetMapping
    @Operation(summary = "find all auctions")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Auctions are found"),
            @ApiResponse(responseCode = "204", description = "No content")
        }
    )
    @SneakyThrows
    private ResponseEntity<List<AuctionDto>> findAuctions() {
        List<Auction> auctions = auctionService.findAllAuctions();
        if (auctions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(auctionConverter.toDto(auctions));
    }

    @GetMapping("/{auctionId}")
    @Operation(summary = "find auction by id")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Auction is found"),
            @ApiResponse(responseCode = "204", description = "No content")
        }
    )
    private ResponseEntity<AuctionDto> findAuctionById(@PathVariable("auctionId") Long auctionId) {
        Auction auction = auctionService.findAuctionById(auctionId);
        if (auction == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(auctionConverter.toDto(auction));
    }

    @PostMapping
    @Operation(summary = "create auction using seller id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Auction is created"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
    private ResponseEntity<CreateAuctionResultDto> createAuction(@RequestBody @Valid CreateAuctionDto createAuctionDto) {
        CreateAuctionResultDto result = auctionService.createAuction(createAuctionDto);
        if (!(result.getMessage() == null)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "update current price for specified auction")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "202", description = "Price has been updated"),
            @ApiResponse(responseCode = "400", description = "Invalid body")
        }
    )
    @PatchMapping("/{auctionId}/price")
    private ResponseEntity<AuctionResultDto> updateAuctionPrice(
        @PathVariable("auctionId") Long auctionId,
        @RequestBody @Valid UpdateAuctionPriceDto auctionPrice) {
        AuctionResultDto result =
            auctionService.updateAuctionPrice(auctionId, auctionPrice.getCurrentPrice());
        if (result.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "update auction date for specified auction")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "202", description = "Price has been updated"),
            @ApiResponse(responseCode = "400", description = "Invalid body")
        }
    )
    @PatchMapping("/{auctionId}/date")
    private ResponseEntity<AuctionResultDto> updateAuctionDate(
        @PathVariable("auctionId") Long auctionId,
        @RequestBody @Valid UpdateAuctionDateDto updateAuctionDateDto
    ) {
        AuctionResultDto result =
            auctionService.updateAuctionStartDate(auctionId, updateAuctionDateDto.getAuctionDate());
        if (result.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "update auction status for specified auction")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "202", description = "Status has been updated"),
            @ApiResponse(responseCode = "400", description = "Invalid body")
        }
    )
    @PatchMapping("/{auctionId}/state")
    private ResponseEntity<AuctionResultDto> updateAuctionState(
        @PathVariable("auctionId") Long auctionId,
        @RequestBody @Valid UpdateAuctionStateDto updateAuctionState
    ) {
        AuctionResultDto result =
            auctionService.updateAuctionState(auctionId, updateAuctionState.getStatus());
        if (result.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/{auctionId}/item")
    private ResponseEntity<AuctionResultDto> updateAuctionItem(
        @PathVariable("auctionId") Long auctionId,
        @RequestBody @Valid AddItemToAuctionDto itemToAuctionDto
    ) {
        AuctionResultDto result = auctionService.addItemToAuction(auctionId, itemToAuctionDto);
        if (result.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PatchMapping("/{auctionId}/item")
    private ResponseEntity<AuctionResultDto> updateAuctionItem(
        @PathVariable("auctionId") Long auctionId,
        @RequestBody @Valid UpdateAuctionItemDto auctionItemDto
    ) {
        AuctionResultDto result = auctionService.updateAuctionItem(
            auctionId, auctionItemDto.getItemId());
        if (result.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "delete specified auction and all related sellers/buyers/items")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "202", description = "Auction has been deleted")
        }
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{auctionId}")
    private void deleteAuction(@PathVariable("auctionId") Long id) {
        auctionService.deleteAuction(id);
    }

    @Hidden
    @GetMapping("/{auctionId}/buyer")
    private ResponseEntity<UserEmailDto> findBuyerEmailByAuctionId(@PathVariable("auctionId") Long id) {
        UserEmailDto buyerEmail = auctionService.findUserByAuctionId(id, "buyer");
        if (buyerEmail.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(buyerEmail);
    }

    @Hidden
    @GetMapping("/{auctionId}/seller")
    private ResponseEntity<UserEmailDto> findSellerEmailByAuctionId(@PathVariable("auctionId") Long id) {
        UserEmailDto sellerEmail = auctionService.findUserByAuctionId(id, "seller");
        if (sellerEmail.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(sellerEmail);
    }

    @Hidden
    @PatchMapping("/{auctionId}/pay")
    private void updateIsPayedToTrue(@PathVariable("auctionId") Long auctionId) {
        auctionService.updateIsPayedToTrue(auctionId);
    }
}
