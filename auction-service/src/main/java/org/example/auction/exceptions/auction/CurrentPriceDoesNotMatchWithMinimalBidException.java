package org.example.auction.exceptions.auction;

public class CurrentPriceDoesNotMatchWithMinimalBidException extends RuntimeException {

    public CurrentPriceDoesNotMatchWithMinimalBidException(String message) {
        super(message);
    }
}
