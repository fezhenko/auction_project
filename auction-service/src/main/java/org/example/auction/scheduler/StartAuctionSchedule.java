package org.example.auction.scheduler;

import org.example.auction.service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StartAuctionSchedule {

    private final AuctionService auctionService;

    @Scheduled(zone = "${custom.scheduler.zone}", cron = "${custom.scheduler.start-auction-cron}")
    protected void startAuction() {

        auctionService.startAuction();
    }
}
