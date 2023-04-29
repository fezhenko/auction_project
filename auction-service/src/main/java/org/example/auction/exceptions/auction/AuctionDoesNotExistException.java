package org.example.auction.exceptions.auction;

public class AuctionDoesNotExistException extends RuntimeException {

    public AuctionDoesNotExistException(String message) {
        super(message);
    }
}
