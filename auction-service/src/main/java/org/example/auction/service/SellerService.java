package org.example.auction.service;

import lombok.AllArgsConstructor;
import org.example.auction.model.Seller;
import org.example.auction.repository.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;

    public List<Seller> findAllSellers() {
        return sellerRepository.findAll();
    }

    public Seller findSellerById(Long id) {
        return sellerRepository.findSellerById(id);
    }

    public void createSeller() {
        sellerRepository.createSeller();
    }

    public void deleteSellerById(Long id) {
        sellerRepository.deleteSellerData(id);
    }
}
