package org.example.auction.exceptions.auction;

public class AuctionDateUpdatedByPastDateException extends RuntimeException {

    public AuctionDateUpdatedByPastDateException(String message) {
        super(message);
    }
}
