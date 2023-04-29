package org.example.auction.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.dto.seller.CreateSellerDto;
import org.example.auction.exceptions.seller.UserEmailIsNullException;
import org.example.auction.model.Seller;
import org.example.auction.repository.SellerRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SellerService {
    private final SellerRepository sellerRepository;

    public List<Seller> findAllSellers() {
        return sellerRepository.findAll();
    }

    public Seller findSellerById(Long id) {
        return sellerRepository.findSellersBySellerId(id);
    }

    public void createSeller(CreateSellerDto sellerDto) {
        if (sellerDto.getEmail() == null) {
            log.error("user email cannot be null");
            throw new UserEmailIsNullException("user email cannot be null");
        }
        sellerRepository.createSeller(sellerDto.getEmail());
        log.info("user with email: '%s' created auction".formatted(sellerDto.getEmail()));
    }

    public void deleteSellerById(Long id) {
        sellerRepository.deleteSellerBySellerId(id);
    }

    public Seller findSellerByEmail(String sellerEmail) {
        return sellerRepository.findSellerByEmail(sellerEmail);
    }
}
