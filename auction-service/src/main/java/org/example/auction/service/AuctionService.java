package org.example.auction.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.dto.auction.AddItemToAuctionDto;
import org.example.auction.dto.auction.AuctionResultDto;
import org.example.auction.dto.auction.UserEmailDto;
import org.example.auction.model.Auction;
import org.example.auction.model.Seller;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.SellerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final SellerRepository sellerRepository;

    public List<Auction> findAllAuctions() {
        return auctionRepository.findAllAuctions();
    }

    public Auction findAuctionById(Long id) {
        return auctionRepository.findAuctionById(id);
    }

    public AuctionResultDto updateAuctionPrice(Long id, Double currentPrice) {

        Auction auction = auctionRepository.findAuctionById(id);
        if (auction.getStartPrice() == 0) {
            log.error("current price cannot be updated until item add to auction");
            return AuctionResultDto.builder()
                .message("current price cannot be updated until item add to auction").build();
        }
        if (currentPrice < auction.getStartPrice()) {
            log.error("current price: %s is less than start price: %s"
                .formatted(currentPrice, auction.getStartPrice()));
            return AuctionResultDto.builder()
                .message("current price: %s is less than start price: %s"
                    .formatted(currentPrice, auction.getStartPrice()))
                .build();
        }
        if ((currentPrice - auction.getStartPrice()) < auction.getMinimalBid()) {
            log.error("current price: %s doesn't match with minimal bid: %s"
                .formatted(currentPrice, auction.getMinimalBid()));
            return AuctionResultDto.builder()
                .message("current price: %s doesn't match with minimal bid: %s"
                    .formatted(currentPrice, auction.getMinimalBid()))
                .build();
        }
        auctionRepository.updateAuctionPrice(id, currentPrice);
        auctionRepository.updateLastUpdatedTime(id);
        return AuctionResultDto.builder().build();
    }

    public void deleteAuction(Long id) {
        auctionRepository.deleteAuction(id);
    }

    public AuctionResultDto updateAuctionStartDate(Long id, Date auctionDate) {
        Auction auction = auctionRepository.findAuctionById(id);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(id));
            return AuctionResultDto.builder()
                .message("auction with id:'%s' doesn't exist".formatted(id)).build();
        }

        LocalDateTime auctionDateToLocalDate = auctionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int compareDateResult = auctionDateToLocalDate.compareTo(LocalDateTime.now());
        Timestamp newAuctionDate = new Timestamp(auctionDate.getTime());

        if (auction.getAuctionDate() == null && compareDateResult <= 0) {
            log.error("auction date can be updated with feature date only, now date:'%s'".formatted(newAuctionDate));
            return AuctionResultDto.builder()
                .message("auction date can be updated with feature date only, now date:'%s'"
                    .formatted(newAuctionDate)).build();
        }
        if (auction.getAuctionDate() != null && compareDateResult <= 0) {
            log.error("auction date can be updated with feature date only, now date:'%s'".formatted(newAuctionDate));
            return AuctionResultDto.builder()
                .message("auction date can be updated with feature date only, now date:'%s'"
                    .formatted(newAuctionDate)).build();
        }
        auctionRepository.updateAuctionDate(id, newAuctionDate);
        return AuctionResultDto.builder().build();
    }

    public AuctionResultDto updateAuctionState(Long auctionId, String status) {
        Auction auction = auctionRepository.findAuctionById(auctionId);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(auctionId));
            return AuctionResultDto.builder()
                .message("auction with id:'%s' doesn't exist".formatted(auctionId)).build();
        }
        if (auction.getAuctionState().equals("FINISHED") &&
            (status.equalsIgnoreCase("IN_PROGRESS") || status.equalsIgnoreCase("PLANNED"))
        ) {
            log.error("auctions with 'FINISHED' status cannot be updated by '%s' status".formatted(status));
            return AuctionResultDto.builder()
                .message("auctions with 'FINISHED' status cannot be updated by '%s' status".formatted(status))
                .build();
        }
        if (auction.getAuctionState().equals("IN_PROGRESS") && status.equalsIgnoreCase("PLANNED")
        ) {
            log.error("auctions with 'IN_PROGRESS' status cannot be updated by '%s' status".formatted(status));
            return AuctionResultDto.builder()
                .message("auctions with 'IN_PROGRESS' status cannot be updated by '%s' status".formatted(status))
                .build();
        }
        auctionRepository.updateAuctionState(auctionId, status.toUpperCase());
        return AuctionResultDto.builder().build();
    }

    public AuctionResultDto updateAuctionItem(Long id, Long itemId) {
        Auction auction = auctionRepository.findAuctionById(id);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(id));
            return AuctionResultDto.builder()
                .message("auction with id:'%s' doesn't exist".formatted(id)).build();
        }
        auctionRepository.updateAuctionItem(id, itemId);
        return AuctionResultDto.builder().build();
    }

    @Scheduled(zone = "ECT", cron = "3 * * * * 0-6")
    private void startAuction() {
        List<Auction> auctionsList = auctionRepository.findAllAuctions();
        Long currentDate = System.currentTimeMillis();
        for (Auction auction : auctionsList) {
            if (auction.getAuctionDate() != null) {
                Long plannedTime = auction.getAuctionDate().getTime();
                if ((plannedTime - currentDate) <= 0 && auction.getAuctionState().equals("PLANNED")) {
                    log.info("auction with id:'%d' starts now".formatted(auction.getAuctionId()));
                    auctionRepository.updateAuctionStateBySchedule("IN_PROGRESS", auction.getAuctionId());
                    auctionRepository.updateLastUpdatedTime(auction.getAuctionId());
                }
                if ((plannedTime - currentDate) > 0 && auction.getAuctionState().equals("PLANNED")) {
                    log.info("Auction '%d' will be started in '%d' minutes".formatted(
                            auction.getAuctionId(), convertMillisecondsToMinutes(plannedTime - currentDate)
                        )
                    );
                }
            }
        }
    }

    //if currentDate>plannedDate on 6 hours and auctionStatus = in_progress change status to finished
    @Scheduled(zone = "ECT", cron = "4 * * * * 0-6")
    private void finishAuction() {
        List<Auction> auctionsList = auctionRepository.findAllAuctions();
        long currentDate = System.currentTimeMillis();
        for (Auction auction : auctionsList) {
            if (auction.getAuctionDate() != null) {
                long plannedTime = auction.getAuctionDate().getTime();
                if ((currentDate - plannedTime) >= 21600000 && auction.getAuctionState().equals("IN_PROGRESS")) {
                    log.info("auction with id:'%d' finished".formatted(auction.getAuctionId()));
                    auctionRepository.updateAuctionStateBySchedule("FINISHED", auction.getAuctionId());
                    auctionRepository.updateAuctionFinalPrice(auction.getCurrentPrice(), auction.getAuctionId());
                    auctionRepository.updateLastUpdatedTime(auction.getAuctionId());
                    //todo: change item status to SOLD
                }
            }
        }
    }

    private int convertMillisecondsToMinutes(Long milliseconds) {
        return (int) ((milliseconds / (1000 * 60)) % 60);
    }

    public UserEmailDto findUserByAuctionId(Long id, String userType) {
        String email;
        if (userType.equals("buyer")) {
            email = auctionRepository.findBuyerEmailByAuctionId(id);
        } else {
            email = auctionRepository.findSellerEmailByAuctionId(id);
        }
        if (email == null) {
            log.error("user email for auction with id:'%d' is null".formatted(id));
            return UserEmailDto.builder().build();
        } else {
            return UserEmailDto.builder().email(email).build();
        }
    }

    public void updateIsPayedToTrue(Long auctionId) {
        if (auctionId == null) {
            log.error("auction id is null");
        }
        auctionRepository.updateIsPayedToTrue(auctionId);
        log.info("auction with id:'%d' has been payed".formatted(auctionId));
    }

    public AuctionResultDto addItemToAuction(Long auctionId, AddItemToAuctionDto itemToAuctionDto) {
        if (auctionId == null) {
            log.error("auction id is null");
            return AuctionResultDto.builder().message("auction id is null").build();
        }
        Auction auction = auctionRepository.findAuctionById(auctionId);
        if (auction.getItemId() != null) {
            log.error("auction with id:'%d' already has item".formatted(auctionId));
            return AuctionResultDto.builder()
                .message("auction with id:'%d' already has item".formatted(auctionId)).build();
        }
        Seller seller = sellerRepository.findSellerById(auction.getSellerId());
        if (seller.getEmail().equals(itemToAuctionDto.getEmail())) {
            auctionRepository.addItemToAuction(auctionId, itemToAuctionDto.getItemId(), itemToAuctionDto.getPrice(),
                itemToAuctionDto.getPrice() * 0.05);
            return AuctionResultDto.builder().build();
        }
        log.error("seller email doesn't match with item owner");
        return AuctionResultDto.builder().message("seller email doesn't match with item owner").build();
    }

    public Auction findAuctionBySellerEmail(String email) {
        return auctionRepository.findAuctionBySellerEmail(email);
    }
}
