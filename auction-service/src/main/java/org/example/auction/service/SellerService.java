package org.example.auction.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.dto.seller.CreateSellerDto;
import org.example.auction.dto.seller.CreateSellerResultDto;
import org.example.auction.model.Seller;
import org.example.auction.repository.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SellerService {
    private final SellerRepository sellerRepository;

    public List<Seller> findAllSellers() {
        return sellerRepository.findAll();
    }

    public Seller findSellerById(Long id) {
        return sellerRepository.findSellerById(id);
    }

    public CreateSellerResultDto createSeller(CreateSellerDto sellerDto) {
        if (sellerDto.getEmail().isEmpty()) {
            log.error("user email cannot be null");
            return CreateSellerResultDto.builder().message("user email cannot be null").build();
        }
        sellerRepository.createSeller(sellerDto.getEmail());
        log.info("user with email: '%s' created auction".formatted(sellerDto.getEmail()));
        return CreateSellerResultDto.builder().build();
    }

    public void deleteSellerById(Long id) {
        sellerRepository.deleteSellerData(id);
    }
}
