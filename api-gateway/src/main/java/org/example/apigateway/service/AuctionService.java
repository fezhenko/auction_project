package org.example.apigateway.service;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apigateway.client.AuctionsClient;
import org.example.apigateway.client.ItemClient;
import org.example.apigateway.client.SellerClient;
import org.example.apigateway.client.dto.SellerDto;
import org.example.apigateway.dto.auction.AddItemToAuctionDto;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.items.AddItemDto;
import org.example.apigateway.dto.items.ItemDto;
import org.example.apigateway.dto.seller.CreateSellerDto;
import org.example.apigateway.exceptions.CreateAuctionException;
import org.example.apigateway.exceptions.UserEmailIsNullException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AuctionService {

    private final AuctionsClient auctionsClient;
    private final SellerClient sellerClient;
    private final ItemClient itemClient;

    public AuctionDto createAuction(User user) throws CreateAuctionException, UserEmailIsNullException {
        try {
            CreateSellerDto createSellerRequest = CreateSellerDto.builder().email(user.getUsername()).build();
            SellerDto seller = sellerClient.createNewSeller(createSellerRequest);
            return auctionsClient.createAuction(seller.getSellerId());
        } catch (Exception exception) {
            throw new CreateAuctionException("Auction creation fails, please try again later");
        }
    }

    public void addItemToAuction(User user, Long auctionId, AddItemDto addItemDto) {
        ItemDto item = itemClient.findItem(addItemDto.getItemId());
        AddItemToAuctionDto itemToAuction = AddItemToAuctionDto.builder().email(user.getUsername()).itemId(item.getId()).price(item.getPrice()).build();
        auctionsClient.addItemToAuction(auctionId, itemToAuction);
    }

    public List<AuctionDto> findAllAuction() {
        return auctionsClient.getAllAuctions();
    }
}
