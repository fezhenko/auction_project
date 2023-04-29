package org.example.auction.exceptions.bid;

public class AddBidToNotStartedAuctionException extends RuntimeException {

    public AddBidToNotStartedAuctionException(String message) {
        super(message);
    }
}
