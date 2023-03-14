package org.example.auction.service;

import lombok.AllArgsConstructor;
import org.example.auction.model.Seller;
import org.example.auction.repository.SellersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SellersService {
    private final SellersRepository sellersRepository;

    public List<Seller> findAllSellers() {
        return sellersRepository.findAll();
    }

    public Seller findSellerById(Long id) {
        return sellersRepository.findSellerById(id);
    }

    public void createSeller(Long auctionId) {
        sellersRepository.createSeller(auctionId);
    }

    public void deleteSellerById(Long id) {
        sellersRepository.deleteSellerData(id);
    }
}
