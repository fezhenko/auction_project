package org.example.auction.exceptions.bid;

public class BidAmountLessThanCurrentPriceException extends RuntimeException {

    public BidAmountLessThanCurrentPriceException(String message) {
        super(message);
    }
}
