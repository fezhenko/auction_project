package org.example.auction.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.auction.converter.SellersConverter;
import org.example.auction.dto.seller.CreateSellerDto;
import org.example.auction.dto.seller.SellerDto;
import org.example.auction.model.Seller;
import org.example.auction.service.SellersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sellers")
@Tag(name = "Sellers")
@AllArgsConstructor
public class SellersController {

    private final SellersService sellersService;
    private final SellersConverter sellersConverter;

    @GetMapping
    @Operation(summary = "find all existed sellers")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "List of sellers"),
                    @ApiResponse(responseCode = "204", description = "No content")
            })
    public ResponseEntity<List<SellerDto>> findAllSellers() {
        List<Seller> sellers = sellersService.findAllSellers();
        if (sellers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sellersConverter.toDto(sellers));
    }

    @Operation(summary = "find user by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "List of sellers"),
                    @ApiResponse(responseCode = "204", description = "No content")
            })
    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerDto> findUserById(@PathVariable("sellerId") Long id) {
        Seller seller = sellersService.findSellerById(id);
        if (seller == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sellersConverter.toDto(seller));
    }

    @Operation(summary = "Create new seller")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Seller created"),
                    @ApiResponse(responseCode = "400")
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void findSellerById(@RequestBody @Valid CreateSellerDto seller) {
        sellersService.createSeller(seller.getAuctionId());
    }

    @Hidden
    @Operation(summary = "Delete a seller")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "Seller deleted"),
                    @ApiResponse(responseCode = "204", description = "User with 'id' does not exist")
            })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{sellerId}")
    public void deleteSeller(@PathVariable("sellerId") Long id) {
        sellersService.deleteSellerById(id);
    }


}
