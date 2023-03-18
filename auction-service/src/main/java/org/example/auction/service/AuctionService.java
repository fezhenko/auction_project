package org.example.auction.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.dto.auction.UpdateCurrentPriceResultDto;
import org.example.auction.model.Auction;
import org.example.auction.repository.AuctionRepository;
import org.springframework.stereotype.Service;

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

    public UpdateCurrentPriceResultDto updateAuctionPrice(Long id, Double currentPrice) {

        Auction auction = auctionRepository.findAuctionById(id);
        if (auction.getStartPrice() == 0) {
            log.error("current price cannot be updated until item add to auction");
            return UpdateCurrentPriceResultDto.builder()
                    .message("current price cannot be updated until item add to auction").build();
        }
        if (currentPrice < auction.getStartPrice()) {
            log.error("current price: %s is less than start price: %s"
                    .formatted(currentPrice, auction.getStartPrice()));
            return UpdateCurrentPriceResultDto.builder()
                    .message("current price: %s is less than start price: %s"
                            .formatted(currentPrice, auction.getStartPrice()))
                    .build();
        }
        if ((currentPrice - auction.getStartPrice()) < auction.getMinimalBid()) {
            log.error("current price: %s doesn't match with minimal bid: %s"
                    .formatted(currentPrice, auction.getMinimalBid()));
            return UpdateCurrentPriceResultDto.builder()
                    .message("current price: %s doesn't match with minimal bid: %s"
                            .formatted(currentPrice, auction.getMinimalBid()))
                    .build();
        }
        auctionRepository.updateAuctionPrice(id, currentPrice);
        return UpdateCurrentPriceResultDto.builder().build();
    }

    public void deleteAuction(Long id) {
        auctionRepository.deleteAuction(id);
    }
}
