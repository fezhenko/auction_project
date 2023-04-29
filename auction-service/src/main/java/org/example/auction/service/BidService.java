package org.example.auction.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.exceptions.bid.AddBidToNotStartedAuctionException;
import org.example.auction.exceptions.bid.BidAmountIsZeroException;
import org.example.auction.exceptions.bid.BidAmountLessThanCurrentPrice;
import org.example.auction.exceptions.bid.BidDoesNotExistException;
import org.example.auction.exceptions.buyer.BuyerDoesNotExistException;
import org.example.auction.model.Auction;
import org.example.auction.model.Bid;
import org.example.auction.model.Buyer;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.BidRepository;
import org.example.auction.repository.BuyerRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BidService {

    private final BidRepository bidRepository;
    private final BuyerRepository buyerRepository;
    private final AuctionRepository auctionRepository;

    public List<Bid> findAllBids() {
        return bidRepository.findAllBids();
    }

    public Bid findBidById(Long bidId) {
        return bidRepository.findBidByBidId(bidId);
    }

    public void createBid(Long auctionId, Double amount, String email) {
        if (amount < 0) {
            log.error("bid amount should be more than zero");
            throw new BidAmountIsZeroException("bid amount should be more than zero");
        }
        Buyer buyer = buyerRepository.findBuyerByEmail(email);
        if (buyer == null) {
            log.error("buyer with specified email does not exist");
            throw new BuyerDoesNotExistException("buyer with specified email:'%s' does not exist".formatted(email));
        }
        Auction auction = auctionRepository.findAuctionByAuctionId(auctionId);
        if (auction.getAuctionState().equals("PLANNED") || auction.getAuctionState().equals("FINISHED")) {
            log.error("auction with id '%d' in not in progress, bid is canceled".formatted(auctionId));
            throw new AddBidToNotStartedAuctionException("auction with id '%d' in not in progress, bid is canceled".formatted(auctionId));
        }
        if (amount > auction.getCurrentPrice()) {
            bidRepository.createBid(amount, buyer.getId());
            auctionRepository.updateBuyerIdForAuction(amount, buyer.getId(), auctionId);
            return;
        }
        throw new BidAmountLessThanCurrentPrice(
                "bid amount: %s less than current price: %s".formatted(amount, auction.getCurrentPrice())
        );
    }

    public void updateBid(Double amount, Long bidId) {
        if (bidRepository.findBidByBidId(bidId) == null) {
            log.error("bid with id:'%d' doesn't exist".formatted(bidId));
            throw new BidDoesNotExistException("bid with id:'%d' doesn't exist".formatted(bidId));
        }
        if (amount < 0) {
            log.error("bid amount should be more than zero");
            throw new BidAmountIsZeroException("bid amount should be more than zero");
        }
        bidRepository.updateBidById(amount, bidId);
    }

    public void deleteBid(Long bidId) {
        if (bidRepository.findBidByBidId(bidId) == null) {
            log.error("bid with id:'%d' doesn't exist".formatted(bidId));
            throw new BidDoesNotExistException("bid with id:'%d' doesn't exist".formatted(bidId));
        }
        bidRepository.deleteBidByBidId(bidId);
    }
}
