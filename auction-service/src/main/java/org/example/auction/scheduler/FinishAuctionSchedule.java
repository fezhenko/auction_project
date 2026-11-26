package org.example.auction.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishAuctionSchedule {

    private final AuctionService auctionService;

    // finishes IN_PROGRESS auctions once auction.duration has passed since their start time
    @Scheduled(zone = "${auction.scheduler.zone}", cron = "${auction.scheduler.finish-cron}")
    protected void finishAuction() {

        auctionService.finishAuction();
    }
}
