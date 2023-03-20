package org.example.auction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.dto.auction.UpdateAuctionDateDto;
import org.example.auction.dto.auction.UpdateAuctionItemDto;
import org.example.auction.dto.auction.UpdateAuctionResultDto;
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

    @Operation(summary = "update current price for specified auction")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "Price has been updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid body")
            }
    )
    @PatchMapping("/{auctionId}/price")
    private ResponseEntity<UpdateAuctionResultDto> updateAuctionPrice(
            @PathVariable("auctionId") Long auctionId,
            @RequestBody @Valid UpdateAuctionPriceDto auctionPrice) {
        UpdateAuctionResultDto result =
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
    private ResponseEntity<UpdateAuctionResultDto> updateAuctionDate(
            @PathVariable("auctionId") Long auctionId,
            @RequestBody @Valid UpdateAuctionDateDto updateAuctionDateDto
    ) {
        UpdateAuctionResultDto result =
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
    private ResponseEntity<UpdateAuctionResultDto> updateAuctionState(
            @PathVariable("auctionId") Long auctionId,
            @RequestBody @Valid UpdateAuctionStateDto updateAuctionState
    ) {
        UpdateAuctionResultDto result =
                auctionService.updateAuctionState(auctionId, updateAuctionState.getStatus());
        if (result.getMessage() == null) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PatchMapping("/{auctionId}/item")
    private ResponseEntity<UpdateAuctionResultDto> updateAuctionItem(
            @PathVariable("auctionId") Long auctionId,
            @RequestBody @Valid UpdateAuctionItemDto auctionItemDto
    ) {
        UpdateAuctionResultDto result = auctionService.updateAuctionItem(
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


}
