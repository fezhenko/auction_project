package org.example.auction.exceptions.buyer;

public class BuyerDoesNotExistException extends RuntimeException {

    public BuyerDoesNotExistException(String message) {
        super(message);
    }
}
