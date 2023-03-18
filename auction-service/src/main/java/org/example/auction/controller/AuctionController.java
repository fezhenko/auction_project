package org.example.auction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.dto.auction.UpdateAuctionPriceDto;
import org.example.auction.dto.auction.UpdateCurrentPriceResultDto;
import org.example.auction.model.Auction;
import org.example.auction.service.AuctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @Operation(summary = "Find all auctions")
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
    @Operation(summary = "Find auction by id")
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
                    @ApiResponse(responseCode = "200", description = "OK")
            }
    )
    @PutMapping("/{auctionId}")
    private ResponseEntity<UpdateCurrentPriceResultDto> updateAuctionPriceDto(@PathVariable("auctionId") Long auctionId,
                                                                              @RequestBody @Valid UpdateAuctionPriceDto auctionPrice) {
        UpdateCurrentPriceResultDto result =
                auctionService.updateAuctionPrice(auctionId, auctionPrice.getCurrentPrice());
        if (result.getMessage().isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @DeleteMapping("/{auctionId}")
    private void deleteAuction(@PathVariable("auctionId") Long id) {
        //todo: удалить аукцион и все зависимости
        auctionService.deleteAuction(id);
    }

}
