package org.example.auction.exceptions.auction;

public class CurrentPriceLessThanStartPriceException extends RuntimeException {

    public CurrentPriceLessThanStartPriceException(String message) {
        super(message);
    }
}
