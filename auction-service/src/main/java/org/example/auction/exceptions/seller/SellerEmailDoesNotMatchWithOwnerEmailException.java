package org.example.auction.exceptions.seller;

public class SellerEmailDoesNotMatchWithOwnerEmailException extends RuntimeException {

    public SellerEmailDoesNotMatchWithOwnerEmailException(String message) {
        super(message);
    }
}
