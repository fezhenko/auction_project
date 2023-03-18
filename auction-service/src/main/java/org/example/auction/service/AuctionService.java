package org.example.auction.service;

import lombok.AllArgsConstructor;
import org.example.auction.model.Auction;
import org.example.auction.repository.AuctionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public List<Auction> findAllAuctions() {
        return auctionRepository.findAllAuctions();
    }
}
