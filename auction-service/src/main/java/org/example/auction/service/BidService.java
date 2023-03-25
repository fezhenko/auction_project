package org.example.auction.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.dto.bid.UpdateBidResultDto;
import org.example.auction.model.Bid;
import org.example.auction.repository.BidRepository;
import org.example.auction.repository.BuyerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BidService {
    private final BidRepository bidRepository;
    private final BuyerRepository buyerRepository;

    public List<Bid> findAllBids() {
        return bidRepository.findAllBids();
    }

    public Bid findBidById(Long bidId) {
        return bidRepository.findBidById(bidId);
    }

    public UpdateBidResultDto createBid(Double amount, String email) {
        if (amount < 0) {
            log.error("bid amount should be more than zero");
            return UpdateBidResultDto.builder().message("bid amount should be more than zero").build();
        }
        Long buyerId = buyerRepository.findBuyerByEmail(email);
        if (buyerId == null) {
            log.error("buyer with '%s' email does not exist");
            return UpdateBidResultDto.builder().message("buyer with '%s' email does not exist".formatted(email)).build();
        }
        bidRepository.createBid(amount, buyerId);
        return UpdateBidResultDto.builder().build();
    }

    public UpdateBidResultDto updateBid(Double amount, Long bidId) {
        if (bidRepository.findBidById(bidId) == null) {
            log.error("bid with id:'%d' doesn't exist".formatted(bidId));
            return UpdateBidResultDto.builder().message("bid with id:'%d' doesn't exist".formatted(bidId)).build();
        }
        if (amount < 0) {
            log.error("bid amount should be more than zero");
            return UpdateBidResultDto.builder().message("bid amount should be more than zero").build();
        }
        bidRepository.updateBidById(amount, bidId);
        return UpdateBidResultDto.builder().build();
    }

    public UpdateBidResultDto deleteBid(Long bidId) {
        if (bidRepository.findBidById(bidId) == null) {
            log.error("bid with id:'%d' doesn't exist".formatted(bidId));
            return UpdateBidResultDto.builder().message("bid with id:'%d' doesn't exist".formatted(bidId)).build();
        }
        bidRepository.deleteBidById(bidId);
        return UpdateBidResultDto.builder().build();
    }
}
