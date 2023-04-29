package org.example.auction.scheduler;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.model.Auction;
import org.example.auction.repository.AuctionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartAuctionSchedule {

    private final AuctionRepository auctionRepository;

    @Scheduled(zone = "${custom.scheduler.zone}", cron = "${custom.scheduler.start-auction-cron}")
    protected void startAuction() {
        List<Auction> auctionsList = (List<Auction>) auctionRepository.findAll();
        Long currentDate = System.currentTimeMillis();
        for (Auction auction : auctionsList) {
            if (auction.getAuctionDate() != null) {
                Long plannedTime = auction.getAuctionDate().getTime();
                if ((plannedTime - currentDate) <= 0 &&
                    auction.getAuctionState().equals(AuctionStatus.PLANNED.name())) {
                    log.info("auction with id:'%d' starts now".formatted(auction.getAuctionId()));
                    auctionRepository.updateAuctionStateBySchedule(AuctionStatus.IN_PROGRESS, auction.getAuctionId());
                    auctionRepository.updateLastUpdatedTime(auction.getAuctionId());
                }
                if ((plannedTime - currentDate) > 0 && auction.getAuctionState().equals(AuctionStatus.PLANNED.name())) {
                    log.info("Auction '%d' will be started in '%d' minutes".formatted(
                        auction.getAuctionId(),
                        convertMillisecondsToMinutes(plannedTime - currentDate)
                    ));
                }
            }
        }
    }

    private int convertMillisecondsToMinutes(Long milliseconds) {
        return (int) ((milliseconds / (1000 * 60)) % 60);
    }

}
