package org.example.auction.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auction.dto.auction.AddItemToAuctionDto;
import org.example.auction.dto.auction.CreateAuctionDto;
import org.example.auction.exceptions.auction.AuctionDateUpdatedByPastDateException;
import org.example.auction.exceptions.auction.AuctionDoesNotExistException;
import org.example.auction.exceptions.auction.AuctionStatusCannotBeUpdatedException;
import org.example.auction.exceptions.auction.CurrentPriceDoesNotMatchWithMinimalBidException;
import org.example.auction.exceptions.auction.CurrentPriceLessThanStartPriceException;
import org.example.auction.exceptions.auction.ItemAlreadyExistException;
import org.example.auction.exceptions.auction.ItemIsNotAddedToAuctionException;
import org.example.auction.exceptions.seller.SellerEmailDoesNotMatchWithOwnerEmailException;
import org.example.auction.exceptions.seller.SellerIdIsNullException;
import org.example.auction.model.Auction;
import org.example.auction.model.Seller;
import org.example.auction.repository.AuctionRepository;
import org.example.auction.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final SellerRepository sellerRepository;

    public List<Auction> findAllAuctions() {
        return (List<Auction>) auctionRepository.findAll();
    }

    public Auction findAuctionById(Long id) {
        return auctionRepository.findAuctionByAuctionId(id);
    }

    public void createAuction(CreateAuctionDto createAuctionDto) {
        if (createAuctionDto.getSellerId() == null) {
            log.error("auction cannot be created due to seller id is null");
            throw new SellerIdIsNullException("auction cannot be created due to seller id is null");
        }
        auctionRepository.createAuction(createAuctionDto.getSellerId());
        log.info("auction has been created for sellerId:'%d'".formatted(createAuctionDto.getSellerId()));
    }

    public void updateAuctionPrice(Long id, Double currentPrice) {
        Auction auction = auctionRepository.findAuctionByAuctionId(id);
        if (auction.getStartPrice() == 0) {
            log.error("current price cannot be updated until item add to auction");
            throw new ItemIsNotAddedToAuctionException("current price cannot be updated until item add to auction");
        }
        if (currentPrice < auction.getStartPrice()) {
            log.error(
                "current price: %s is less than start price: %s".formatted(currentPrice, auction.getStartPrice()));
            throw new CurrentPriceLessThanStartPriceException(
                "current price: %s is less than start price: %s".formatted(
                    currentPrice,
                    auction.getStartPrice()
                ));
        }
        if ((currentPrice - auction.getStartPrice()) < auction.getMinimalBid()) {
            log.error("current price: %s doesn't match with minimal bid: %s".formatted(currentPrice,
                auction.getMinimalBid()));
            throw new CurrentPriceDoesNotMatchWithMinimalBidException(
                "current price: %s doesn't match with minimal bid: %s".formatted(
                    currentPrice,
                    auction.getMinimalBid()
                ));
        }
        auctionRepository.updateAuctionPrice(id, currentPrice);
        auctionRepository.updateLastUpdatedTime(id);
    }

    public void deleteAuction(Long id) {
        auctionRepository.deleteAuctionByAuctionId(id);
    }

    public void updateAuctionStartDate(Long id, Date auctionDate) {
        Auction auction = auctionRepository.findAuctionByAuctionId(id);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(id));
            throw new AuctionDoesNotExistException("auction with id:'%s' doesn't exist".formatted(id));
        }

        LocalDateTime auctionDateToLocalDate = auctionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int compareDateResult = auctionDateToLocalDate.compareTo(LocalDateTime.now());
        Timestamp newAuctionDate = new Timestamp(auctionDate.getTime());

        if (compareDateResult <= 0) {
            log.error("auction date can be updated with feature date only, now date:'%s'".formatted(newAuctionDate));
            throw new AuctionDateUpdatedByPastDateException(
                "auction date can be updated with feature date only, now date:'%s'".formatted(newAuctionDate));
        }

        auctionRepository.updateAuctionDate(id, newAuctionDate);
    }

    public void updateAuctionState(Long auctionId, String status) {
        Auction auction = auctionRepository.findAuctionByAuctionId(auctionId);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(auctionId));
            throw new AuctionDoesNotExistException("auction with id:'%s' doesn't exist".formatted(auctionId));
        }
        if (auction.getAuctionState().equals("FINISHED") &&
            (status.equalsIgnoreCase("IN_PROGRESS") || status.equalsIgnoreCase("PLANNED"))) {
            log.error("auctions with 'FINISHED' status cannot be updated by '%s' status".formatted(status));
            throw new AuctionStatusCannotBeUpdatedException(
                "auctions with 'FINISHED' status cannot be updated by '%s' status".formatted(status));
        }
        if (auction.getAuctionState().equals("IN_PROGRESS") && status.equalsIgnoreCase("PLANNED")) {
            log.error("auctions with 'IN_PROGRESS' status cannot be updated by '%s' status".formatted(status));
            throw new AuctionStatusCannotBeUpdatedException(
                "auctions with 'IN_PROGRESS' status cannot be updated by '%s' status".formatted(status));
        }
        auctionRepository.updateAuctionState(auctionId, status.toUpperCase());
    }

    public void updateAuctionItem(Long id, Long itemId) {
        Auction auction = auctionRepository.findAuctionByAuctionId(id);
        if (auction == null) {
            log.error("auction with '%s' doesn't exist".formatted(id));
            throw new AuctionDoesNotExistException("auction with id:'%s' doesn't exist".formatted(id));
        }
        auctionRepository.updateAuctionItem(id, itemId);
    }

    public void updateIsPayedToTrue(Long auctionId) {
        if (auctionId == null) {
            log.error("auction id is null");
        }
        auctionRepository.updateIsPayedToTrue(auctionId);
        log.info("auction with id:'%d' has been payed".formatted(auctionId));
    }

    public void addItemToAuction(Long auctionId, AddItemToAuctionDto itemToAuctionDto) {
        if (auctionId == null) {
            log.error("auction id is null");
            throw new AuctionDoesNotExistException("auction id is null");
        }
        Auction auction = auctionRepository.findAuctionByAuctionId(auctionId);
        if (auction.getItemId() != null) {
            log.error("auction with id:'%d' already has item".formatted(auctionId));
            throw new ItemAlreadyExistException("auction with id:'%d' already has item".formatted(auctionId));
        }
        Seller seller = sellerRepository.findSellersBySellerId(auction.getSellerId());
        if (seller.getEmail().equals(itemToAuctionDto.getEmail())) {
            auctionRepository.addItemToAuction(auctionId, itemToAuctionDto.getItemId(), itemToAuctionDto.getPrice(),
                itemToAuctionDto.getPrice() * 0.05);
            return;
        }
        log.error("seller email doesn't match with item owner");
        throw new SellerEmailDoesNotMatchWithOwnerEmailException("seller email doesn't match with item owner");
    }

    public Auction findAuctionBySellerEmail(String email) {
        return auctionRepository.findAuctionBySellerEmail(email);
    }

    public List<Auction> findAuctionsBySellerId(Long sellerId) {
        if (sellerId == null) {
            throw new SellerIdIsNullException("seller id is null");
        }
        return auctionRepository.findAuctionsBySellerId(sellerId);
    }

    public void deleteAuctionBySellerId(Long sellerId) {
        auctionRepository.deleteAuctionBySellerId(sellerId);
    }
}
