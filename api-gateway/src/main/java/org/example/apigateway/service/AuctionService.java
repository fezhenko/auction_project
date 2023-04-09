package org.example.apigateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.example.apigateway.client.AuctionsClient;
import org.example.apigateway.client.ItemClient;
import org.example.apigateway.client.SellerClient;
import org.example.apigateway.client.UserClient;
import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.dto.auction.AddItemToAuctionDto;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.auction.UserEmailDto;
import org.example.apigateway.dto.auction.FinalPriceDto;
import org.example.apigateway.dto.items.AddItemDto;
import org.example.apigateway.dto.items.ItemDto;
import org.example.apigateway.dto.items.ItemResultDto;
import org.example.apigateway.dto.seller.CreateSellerResultDto;
import org.example.apigateway.dto.seller.CreateSellerDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AuctionService {
    private final AuctionsClient auctionsClient;
    private final UserClient userClient;
    private final SellerClient sellerClient;
    private final ItemClient itemClient;

    @Scheduled(zone = "ECT", cron = "*/60 * * * * 0-7")
    private void updateUserBalance() {
        List<AuctionDto> auctions = auctionsClient.getAllAuctions();
        if (auctions == null) {
            log.info("list of auctions is empty");
        } else {
            List<AuctionDto> todayAuctions = auctions.stream()
                .filter(auction -> auction.getAuctionDate() != null)
                .filter(auction -> auction.getPriceDto().getBuyer() != null)
                .filter(auction -> DateUtils.isSameDay(auction.getAuctionDate(), Calendar.getInstance().getTime()))
                .filter(auction -> auction.getAuctionState().equals("FINISHED"))
                .filter(auction -> !auction.getIsPayed())
                .toList();
            todayAuctions.forEach(
                auction -> {
                    updateBuyerBalanceAfterAuctionFinish(auction);
                    updateSellerBalanceAfterAuctionFinish(auction);
                }
            );
        }
    }

    private void updateBuyerBalanceAfterAuctionFinish(AuctionDto auction) {
        UserEmailDto buyer = auctionsClient.getBuyerEmailByAuctionId(auction.getAuctionId());
        AppUserDto user = userClient.findUserByEmail(buyer.getEmail());
        FinalPriceDto finalPrice = FinalPriceDto.builder().finalPrice(auction.getPriceDto().getFinalPrice()).build();
        userClient.updateUserBalance(user.getId(), "buyer", finalPrice);
        log.info("user id:'%d' balance has been updated".formatted(user.getId()));
        auctionsClient.setIsPayedToTrue(auction.getAuctionId());
        log.info("auction id:'%d' has been payed by buyer:'%d'"
            .formatted(auction.getAuctionId(), auction.getPriceDto().getBuyer()));
    }

    private void updateSellerBalanceAfterAuctionFinish(AuctionDto auction) {
        UserEmailDto seller = auctionsClient.getSellerEmailByAuctionId(auction.getAuctionId());
        AppUserDto user = userClient.findUserByEmail(seller.getEmail());
        FinalPriceDto finalPrice = FinalPriceDto.builder().finalPrice(auction.getPriceDto().getFinalPrice()).build();
        userClient.updateUserBalance(user.getId(), "seller", finalPrice);
        log.info("user id:'%d' balance has been updated after selling an item on auction:'%d'"
            .formatted(user.getId(), auction.getAuctionId()));
    }

    public CreateSellerResultDto createAuction(User user) {
        CreateSellerDto seller = CreateSellerDto.builder().email(user.getUsername()).build();
        return sellerClient.createNewSeller(seller);
    }

    public ItemResultDto addItemToAuction(User user, Long auctionId, AddItemDto addItemDto) {
        ItemDto item = itemClient.findItem(addItemDto.getItemId());
        AddItemToAuctionDto itemToAuction = AddItemToAuctionDto.builder()
            .email(user.getUsername())
            .itemId(item.getId())
            .price(item.getPrice())
            .build();
        return auctionsClient.addItemToAuction(auctionId, itemToAuction);
    }

    public List<AuctionDto> findAllAuction() {
        return auctionsClient.getAllAuctions();
    }
}
