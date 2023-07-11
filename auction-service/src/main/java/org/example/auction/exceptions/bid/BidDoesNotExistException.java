package org.example.auction.exceptions.bid;

public class BidDoesNotExistException extends RuntimeException {

    public BidDoesNotExistException(String message) {
        super(message);
    }
}
