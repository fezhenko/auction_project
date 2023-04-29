package org.example.auction.exceptions.bid;

public class BidAmountLessThanCurrentPrice extends RuntimeException {

    public BidAmountLessThanCurrentPrice(String message) {
        super(message);
    }
}
