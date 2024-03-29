package org.example.auction.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.service.AuctionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateBalanceAfterAuctionFinishSchedule {

    private final AuctionService auctionService;

    @Async
    @Scheduled(zone = "${custom.scheduler.zone}", cron = "${custom.scheduler.update-balance-cron}")
    void updateUserBalance() {

        auctionService.updateUserBalance();
    }
}
