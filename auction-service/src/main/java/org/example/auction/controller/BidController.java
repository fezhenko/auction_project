package org.example.auction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.auction.converter.BidConverter;
import org.example.auction.dto.bid.BidDto;
import org.example.auction.dto.bid.CreateBidDto;
import org.example.auction.dto.bid.UpdateBidDto;
import org.example.auction.dto.bid.UpdateBidResultDto;
import org.example.auction.model.Bid;
import org.example.auction.service.BidService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Bids")
@RestController
@RequestMapping("/api/v1/bids")
@AllArgsConstructor
public class BidController {
    private final BidService bidService;
    private final BidConverter bidConverter;

    @Operation(summary = "find a specific bid")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "bids have been found"),
                    @ApiResponse(responseCode = "204", description = "bids don't exist")
            }
    )
    @GetMapping
    private ResponseEntity<List<BidDto>> findAllBids() {
        List<Bid> bids = bidService.findAllBids();
        if (bids.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bidConverter.toDto(bids));
    }

    @Operation(summary = "find a specific bid")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "bid has been found"),
                    @ApiResponse(responseCode = "204", description = "bid doesn't exist")
            }
    )
    @GetMapping("/{bidId}")
    private ResponseEntity<BidDto> findBidById(@PathVariable("bidId") Long bidId) {
        Bid bid = bidService.findBidById(bidId);
        if (bid == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bidConverter.toDto(bid));
    }

    @Operation(summary = "create new bid")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "bid has been created"),
                    @ApiResponse(responseCode = "400", description = "invalid body")
            }
    )
    @PostMapping
    private ResponseEntity<UpdateBidResultDto> createBid(@RequestBody @Valid CreateBidDto createBidDto) {
        UpdateBidResultDto result = bidService.createBid(createBidDto.getAmount(), createBidDto.getEmail());
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "update specified bid")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "bid has been updated"),
                    @ApiResponse(responseCode = "400", description = "invalid body")
            }
    )
    @PatchMapping("/{bidId}")
    private ResponseEntity<UpdateBidResultDto> updateBid(
            @PathVariable("bidId") Long id, @RequestBody @Valid UpdateBidDto updateBidDto) {
        UpdateBidResultDto result = bidService.updateBid(updateBidDto.getAmount(), id);
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "delete specified bid")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "bid deleted"),
                    @ApiResponse(responseCode = "400", description = "invalid body")
            }
    )
    @DeleteMapping("/{bidId}")
    private ResponseEntity<UpdateBidResultDto> deleteBid(@PathVariable("bidId") Long id) {
        UpdateBidResultDto result = bidService.deleteBid(id);
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.badRequest().body(result);
    }
}
