package org.example.auction.scheduler;

import java.util.Calendar;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.example.auction.converter.AuctionConverter;
import org.example.auction.dto.auction.AuctionDto;
import org.example.auction.exceptions.buyer.BuyerDoesNotExistException;
import org.example.auction.model.Auction;
import org.example.auction.model.Buyer;
import org.example.auction.model.Seller;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.BuyerRepository;
import org.example.auction.repository.SellerRepository;
import org.example.auction.scheduler.client.UserClient;
import org.example.auction.scheduler.client.dto.AppUserDto;
import org.example.auction.scheduler.client.dto.FinalPriceDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateBalanceAfterAuctionFinishSchedule {

    private final AuctionRepository auctionRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final AuctionConverter auctionConverter;
    private final UserClient userClient;

    @Async
    @Scheduled(zone = "${custom.scheduler.zone}", cron = "${custom.scheduler.update-balance-cron}")
    void updateUserBalance() {
        List<AuctionDto> auctions = auctionConverter.toDto((List<Auction>) auctionRepository.findAll());
        if (auctions.isEmpty()) {
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
        Buyer buyer = buyerRepository.findBuyerByAuctionId(auction.getAuctionId());
        if (buyer == null) {
            log.error("Auction with id: %d does not have buyer".formatted(auction.getAuctionId()));
            throw new BuyerDoesNotExistException(
                "Auction with id: %d does not have buyer".formatted(auction.getAuctionId()));
        }
        AppUserDto user = userClient.findUserByEmail(buyer.getEmail());
        FinalPriceDto finalPrice = FinalPriceDto.builder().finalPrice(auction.getPriceDto().getFinalPrice()).build();
        userClient.updateUserBalance(user.getId(), UserType.BUYER.name(), finalPrice);
        log.info("user id:'%d' balance has been updated".formatted(user.getId()));
        auctionRepository.updateIsPayedToTrue(auction.getAuctionId());
        log.info("auction id:'%d' has been payed by buyer:'%d'"
            .formatted(auction.getAuctionId(), auction.getPriceDto().getBuyer()));
    }

    public void updateSellerBalanceAfterAuctionFinish(AuctionDto auction) {
        Seller seller = sellerRepository.findSellerByAuctionId(auction.getAuctionId());
        AppUserDto user = userClient.findUserByEmail(seller.getEmail());
        FinalPriceDto finalPrice = FinalPriceDto.builder().finalPrice(auction.getPriceDto().getFinalPrice()).build();
        userClient.updateUserBalance(user.getId(), UserType.SELLER.name(), finalPrice);
        log.info("user id:'%d' balance has been updated after selling an item on auction:'%d'"
            .formatted(user.getId(), auction.getAuctionId()));
    }

}
