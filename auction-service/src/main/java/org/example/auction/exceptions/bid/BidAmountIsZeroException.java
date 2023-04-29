package org.example.auction.exceptions.bid;

public class BidAmountIsZeroException extends RuntimeException {

    public BidAmountIsZeroException(String message) {
        super(message);
    }
}
