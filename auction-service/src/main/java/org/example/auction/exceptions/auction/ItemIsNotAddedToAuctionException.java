package org.example.auction.exceptions.auction;

public class ItemIsNotAddedToAuctionException extends RuntimeException {

    public ItemIsNotAddedToAuctionException(String message) {
        super(message);
    }
}
