package org.example.auction.scheduler;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.model.Auction;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.scheduler.client.ItemClient;
import org.example.auction.scheduler.client.dto.UpdateItemDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishAuctionSchedule {

    private final AuctionRepository auctionRepository;
    private final ItemClient itemClient;

    //if currentDate>plannedDate on 6 hours and auctionStatus = in_progress change status to finished
    @Scheduled(zone = "${custom.scheduler.zone}", cron = "${custom.scheduler.finish-auction-cron}")
    protected void finishAuction() {
        List<Auction> auctionsList = (List<Auction>) auctionRepository.findAll();
        long currentDate = System.currentTimeMillis();
        for (Auction auction : auctionsList) {
            if (auction.getAuctionDate() != null) {
                long plannedTime = auction.getAuctionDate().getTime();
                if ((currentDate - plannedTime) >= 21600000 &&
                    auction.getAuctionState().equals(AuctionStatus.IN_PROGRESS.name())) {
                    log.info("auction with id:'%d' finished".formatted(auction.getAuctionId()));
                    auctionRepository.updateAuctionStateBySchedule(AuctionStatus.FINISHED,
                        auction.getAuctionId());
                    auctionRepository.updateAuctionFinalPrice(auction.getCurrentPrice(), auction.getAuctionId());
                    auctionRepository.updateLastUpdatedTime(auction.getAuctionId());
                    itemClient.updateItemStatus(auction.getItemId(), UpdateItemDto.builder()
                        .itemStatus("SOLD").build());
                }
            }
        }
    }
}
