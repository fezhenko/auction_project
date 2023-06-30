package org.example.auction.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartAuctionSchedule {

    private final AuctionService auctionService;

    @Scheduled(zone = "${custom.scheduler.zone}", cron = "${custom.scheduler.start-auction-cron}")
    protected void startAuction() {

        auctionService.startAuction();
    }
}
