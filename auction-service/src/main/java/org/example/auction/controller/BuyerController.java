package org.example.auction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.auction.converter.BuyerConverter;
import org.example.auction.dto.buyer.BuyerDto;
import org.example.auction.dto.buyer.CreateBuyerDto;
import org.example.auction.dto.buyer.UpdateBuyerDto;
import org.example.auction.model.Buyer;
import org.example.auction.service.BuyerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/buyers")
@Tag(name = "Buyers")
public class BuyerController {

    private final BuyerService buyerService;
    private final BuyerConverter buyerConverter;

    @GetMapping
    @Operation(summary = "Find all existed buyers")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "List of buyers"),
                    @ApiResponse(responseCode = "204", description = "No buyers")
            }
    )
    private ResponseEntity<List<BuyerDto>> findAll() {
        List<Buyer> buyers = buyerService.findAllBuyers();
        if (buyers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buyerConverter.toDto(buyers));
    }

    @GetMapping("/{buyerId}")
    @Operation(summary = "Find buyer by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "204", description = "No buyers")
            }
    )
    private ResponseEntity<BuyerDto> findUserById(@PathVariable("buyerId") Long id) {
        Buyer buyer = buyerService.findBuyerById(id);
        if (buyer == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(buyerConverter.toDto(buyer));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create buyer")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Check input body and try again")
            }
    )
    private void createBuyer(@RequestBody @Valid CreateBuyerDto buyer) {
        buyerService.createBuyer(buyer.getAuctionId(), buyer.getBidId());
    }

    @PutMapping("/{buyerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Update buyer info")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "Buyer updated"),
                    @ApiResponse(responseCode = "204", description = "No content")
            }
    )
    private void updateBuyer(@PathVariable("buyerId") Long id, @RequestBody @Valid UpdateBuyerDto buyer) {
        buyerService.updateBuyer(id, buyer.getBidId());
    }

    @DeleteMapping("/{buyerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Delete buyer")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "Buyer updated"),
                    @ApiResponse(responseCode = "204", description = "No content")
            }
    )
    private void deleteBuyer(@PathVariable("buyerId") Long id) {
        buyerService.deleteBuyer(id);
    }

}
