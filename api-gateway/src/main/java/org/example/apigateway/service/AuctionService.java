package org.example.apigateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.example.apigateway.client.AuctionsClient;
import org.example.apigateway.client.SellerClient;
import org.example.apigateway.client.UserClient;
import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.auction.BuyerEmailDto;
import org.example.apigateway.dto.auction.FinalPriceDto;
import org.example.apigateway.dto.seller.CreateSellerResultDto;
import org.example.apigateway.dto.seller.CreateSellerDto;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Scheduled(zone = "ECT", cron = "*/10 * * * * 0-7")
    private void updateUserBalance() {
        List<AuctionDto> auctions = auctionsClient.getAllAuctions();
        List<AuctionDto> todayAuctions = auctions.stream()
                .filter(auction -> auction.getAuctionDate() != null)
                .filter(auction -> auction.getPriceDto().getBuyer() != null)
                .filter(auction -> DateUtils.isSameDay(auction.getAuctionDate(), Calendar.getInstance().getTime()))
                .filter(auction -> auction.getAuctionState().equals("FINISHED"))
                .filter(auction -> !auction.getIsPayed())
                .toList();
        todayAuctions.forEach(
                this::updateBuyerBalanceAfterAuctionFinish
        );
    }

    private void updateBuyerBalanceAfterAuctionFinish(AuctionDto auction) {
        BuyerEmailDto buyer = auctionsClient.getBuyerEmailByAuctionId(auction.getAuctionId());
        AppUserDto user = userClient.findUserByEmail(buyer.getEmail());
        FinalPriceDto finalPrice = FinalPriceDto.builder().finalPrice(auction.getPriceDto().getFinalPrice()).build();
        userClient.updateUserBalance(user.getId(), finalPrice);
        auctionsClient.setIsPayedToTrue(auction.getAuctionId());
    }

    public CreateSellerResultDto createAuction(CreateSellerDto sellerDto) {
        CreateSellerResultDto result = sellerClient.createNewSeller(sellerDto);
        if (result == null) {
            return CreateSellerResultDto.builder().build();
        }
        return result;
    }
}
