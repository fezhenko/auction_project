package org.example.auction.exceptions.auction;

public class ItemAlreadyExistException extends RuntimeException {

    public ItemAlreadyExistException(String message) {
        super(message);
    }
}
