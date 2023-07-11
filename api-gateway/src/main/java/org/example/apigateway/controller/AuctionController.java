package org.example.apigateway.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    private List<AuctionDto> findAllAuctions() {
        return auctionService.findAllAuction();
    }


    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created"),
        @ApiResponse(responseCode = "400", description = "Request body is not correct"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SneakyThrows
    private ResponseEntity<AuctionDto> createAuction() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.createAuction(user));
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully added"),
        @ApiResponse(responseCode = "204", description = "Item is not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/{auctionId}/item")
    @ResponseStatus(HttpStatus.OK)
    private void addItemToAuction(
            @PathVariable("auctionId") Long auctionId, @RequestBody @Valid AddItemDto item
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auctionService.addItemToAuction(user, auctionId, item);
    }
}
