package org.example.auction.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.model.Auction;
import org.example.auction.model.Buyer;
import org.example.auction.model.Seller;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.BuyerRepository;
import org.example.auction.repository.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BuyerService {
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final AuctionRepository auctionRepository;

    public List<Buyer> findAllBuyers() {
        return buyerRepository.findAll();
    }

    public Buyer findBuyerById(Long id) {
        return buyerRepository.findBuyerById(id);
    }

    public void createBuyer(String email, Long auctionId) {
        Auction auction = auctionRepository.findAuctionById(auctionId);
        if (isBuyerTheSeller(auction)) {
            log.error("seller cannot be buyer at the same time");
        } else {
            buyerRepository.createBuyer(email, auctionId);
            log.info("buyer has been added to auction:'%d'".formatted(auctionId));
        }
    }

    public void deleteBuyer(Long id) {
        buyerRepository.deleteBuyer(id);
    }

    private Boolean isBuyerTheSeller(Auction auction) {
        Seller seller = sellerRepository.findSellerById(auction.getSellerId());
        Buyer buyer = buyerRepository.findBuyerById(auction.getBuyerId());
        return buyer.getEmail().equals(seller.getEmail());
    }
}
