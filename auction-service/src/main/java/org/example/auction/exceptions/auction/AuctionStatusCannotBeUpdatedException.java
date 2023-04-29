package org.example.auction.exceptions.auction;

public class AuctionStatusCannotBeUpdatedException extends RuntimeException {

    public AuctionStatusCannotBeUpdatedException(String message) {
        super(message);
    }
}
