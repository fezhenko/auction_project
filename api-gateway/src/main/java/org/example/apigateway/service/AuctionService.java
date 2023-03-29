package org.example.apigateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.example.apigateway.client.AuctionsClient;
import org.example.apigateway.client.UserClient;
import org.example.apigateway.client.dto.AppUserDto;
import org.example.apigateway.dto.auction.AuctionDto;
import org.example.apigateway.dto.auction.BuyerEmailDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AuctionService {
    private final AuctionsClient auctionsClient;
    private final HashSet<Long> payedAuctions;
    private final UserClient userClient;

    @Scheduled(zone = "ECT", cron = "1 * * * * 0-7")
    private void updateUserBalance() {
        List<AuctionDto> auctions = auctionsClient.getAllAuctions();
        List<AuctionDto> todayAuctionsIds = auctions.stream()
                .filter(auction -> auction.getAuctionState().equals("FINISHED"))
                .filter(auction -> DateUtils.isSameDay(auction.getAuctionDate(), Calendar.getInstance().getTime()))
                .toList();
        if (payedAuctions.isEmpty()) {
            todayAuctionsIds.forEach(
                auction -> {
                    BuyerEmailDto buyer = auctionsClient.getBuyerEmailByAuctionId(auction.getAuctionId());
                    AppUserDto user = userClient.findUserByEmail(buyer.getEmail());
                    userClient.updateUserBalance(user.getId(), auction.getPriceDto().getFinalPrice());
                    payedAuctions.add(auction.getAuctionId());
                }
            );
        } else {
            todayAuctionsIds.forEach(
                auction -> {
                    if (!payedAuctions.contains(auction.getAuctionId())) {
                        BuyerEmailDto buyer = auctionsClient.getBuyerEmailByAuctionId(auction.getAuctionId());
                        AppUserDto user = userClient.findUserByEmail(buyer.getEmail());
                        userClient.updateUserBalance(user.getId(), auction.getPriceDto().getFinalPrice());
                        payedAuctions.add(auction.getAuctionId());
                    }
                }
            );
        }
    }

}
