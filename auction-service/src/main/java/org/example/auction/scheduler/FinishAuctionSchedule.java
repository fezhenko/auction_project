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


    //if currentDate>plannedDate on 6 hours and auctionStatus = in_progress change status to finished
    @Scheduled(zone = "${custom.scheduler.zone}", cron = "${custom.scheduler.finish-auction-cron}")
    protected void finishAuction() {

        auctionService.finishAuction();
    }
}
