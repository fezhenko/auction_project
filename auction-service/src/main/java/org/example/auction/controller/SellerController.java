package org.example.auction.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.auction.converter.SellerConverter;
import org.example.auction.dto.seller.SellerDto;
import org.example.auction.model.Seller;
import org.example.auction.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sellers")
@Tag(name = "Sellers")
@AllArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final SellerConverter sellerConverter;

    @GetMapping
    @Operation(summary = "find all existed sellers")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "List of sellers"),
                    @ApiResponse(responseCode = "204", description = "No content")
            })
    public ResponseEntity<List<SellerDto>> findAllSellers() {
        List<Seller> sellers = sellerService.findAllSellers();
        if (sellers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sellerConverter.toDto(sellers));
    }

    @Operation(summary = "find user by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "List of sellers"),
                    @ApiResponse(responseCode = "204", description = "No content")
            })
    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerDto> findUserById(@PathVariable("sellerId") Long id) {
        Seller seller = sellerService.findSellerById(id);
        if (seller == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sellerConverter.toDto(seller));
    }

    @Operation(summary = "Create new seller")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Seller created"),
                    @ApiResponse(responseCode = "400")
            })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createSeller() {
        sellerService.createSeller();
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
        sellerService.deleteSellerById(id);
    }


}
