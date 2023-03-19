package org.example.auction.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.dto.auction.UpdateAuctionResultDto;
import org.example.auction.model.Auction;
import org.example.auction.repository.AuctionRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public List<Auction> findAllAuctions() {
        return auctionRepository.findAllAuctions();
    }

    public Auction findAuctionById(Long id) {
        return auctionRepository.findAuctionById(id);
    }

    public UpdateAuctionResultDto updateAuctionPrice(Long id, Double currentPrice) {

        Auction auction = auctionRepository.findAuctionById(id);
        if (auction.getStartPrice() == 0) {
            log.error("current price cannot be updated until item add to auction");
            return UpdateAuctionResultDto.builder()
                    .message("current price cannot be updated until item add to auction").build();
        }
        if (currentPrice < auction.getStartPrice()) {
            log.error("current price: %s is less than start price: %s"
                    .formatted(currentPrice, auction.getStartPrice()));
            return UpdateAuctionResultDto.builder()
                    .message("current price: %s is less than start price: %s"
                            .formatted(currentPrice, auction.getStartPrice()))
                    .build();
        }
        if ((currentPrice - auction.getStartPrice()) < auction.getMinimalBid()) {
            log.error("current price: %s doesn't match with minimal bid: %s"
                    .formatted(currentPrice, auction.getMinimalBid()));
            return UpdateAuctionResultDto.builder()
                    .message("current price: %s doesn't match with minimal bid: %s"
                            .formatted(currentPrice, auction.getMinimalBid()))
                    .build();
        }
        auctionRepository.updateAuctionPrice(id, currentPrice);
        auctionRepository.updateLastUpdatedTime(id);
        return UpdateAuctionResultDto.builder().build();
    }

    public void deleteAuction(Long id) {
        auctionRepository.deleteAuction(id);
    }

    public UpdateAuctionResultDto updateAuctionStartDate(Long id, Date auctionDate) {
        Auction auction = auctionRepository.findAuctionById(id);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(id));
            return UpdateAuctionResultDto.builder()
                    .message("auction with id:'%s' doesn't exist".formatted(id)).build();
        }

        LocalDateTime auctionDateToLocalDate = auctionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int compareDateResult = auctionDateToLocalDate.compareTo(LocalDateTime.now());
        Timestamp newAuctionDate = new Timestamp(auctionDate.getTime());

        if (auction.getAuctionDate() == null && compareDateResult <= 0) {
            log.error("auction date can be updated with feature date only, now date:'%s'".formatted(newAuctionDate));
            return UpdateAuctionResultDto.builder()
                    .message("auction date can be updated with feature date only, now date:'%s'"
                            .formatted(newAuctionDate)).build();
        }
        if (auction.getAuctionDate() != null && compareDateResult <= 0) {
            log.error("auction date can be updated with feature date only, now date:'%s'".formatted(newAuctionDate));
            return UpdateAuctionResultDto.builder()
                    .message("auction date can be updated with feature date only, now date:'%s'"
                            .formatted(newAuctionDate)).build();
        }
        auctionRepository.updateAuctionDate(id, newAuctionDate);
        return UpdateAuctionResultDto.builder().build();
    }

    public UpdateAuctionResultDto updateAuctionState(Long auctionId, String status) {
        Auction auction = auctionRepository.findAuctionById(auctionId);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(auctionId));
            return UpdateAuctionResultDto.builder()
                    .message("auction with id:'%s' doesn't exist".formatted(auctionId)).build();
        }
        if (auction.getAuctionState().equals("FINISHED") &&
                (status.equalsIgnoreCase("IN_PROGRESS") || status.equalsIgnoreCase("PLANNED"))
        ) {
            log.error("auctions with 'FINISHED' status cannot be updated by '%s' status".formatted(status));
            return UpdateAuctionResultDto.builder()
                    .message("auctions with 'FINISHED' status cannot be updated by '%s' status".formatted(status))
                    .build();
        }
        if (auction.getAuctionState().equals("IN_PROGRESS") && status.equalsIgnoreCase("PLANNED")
        ) {
            log.error("auctions with 'IN_PROGRESS' status cannot be updated by '%s' status".formatted(status));
            return UpdateAuctionResultDto.builder()
                    .message("auctions with 'IN_PROGRESS' status cannot be updated by '%s' status".formatted(status))
                    .build();
        }
        auctionRepository.updateAuctionState(auctionId, status.toUpperCase());
        return UpdateAuctionResultDto.builder().build();
    }

    public UpdateAuctionResultDto updateAuctionItem(Long id, Long itemId) {
        Auction auction = auctionRepository.findAuctionById(id);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(id));
            return UpdateAuctionResultDto.builder()
                    .message("auction with id:'%s' doesn't exist".formatted(id)).build();
        }
        auctionRepository.updateAuctionItem(id, itemId);
        return UpdateAuctionResultDto.builder().build();
    }

}
