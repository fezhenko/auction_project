package org.example.auction.service;

import lombok.AllArgsConstructor;
import org.example.auction.model.Buyer;
import org.example.auction.repository.BuyerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BuyerService {
    private final BuyerRepository buyerRepository;

    public List<Buyer> findAllBuyers() {
        return buyerRepository.findAll();
    }

    public Buyer findBuyerById(Long id) {
        return buyerRepository.findBuyerById(id);
    }

    public void createBuyer(String email, Long auctionId) {
        buyerRepository.createBuyer(email, auctionId);
    }

    public void deleteBuyer(Long id) {
        buyerRepository.deleteBuyer(id);
    }
}
