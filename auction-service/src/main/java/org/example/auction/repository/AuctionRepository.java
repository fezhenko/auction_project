package org.example.auction.repository;

import org.example.auction.model.Auction;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AuctionRepository extends Repository<Auction, Long> {

    List<Auction> findAllAuctions();
    @Query("select * from auctions where auction_id = :id")
    Auction findAuctionById(@Param("id") Long id);

    void createAuction(Long sellerId);
    void updateAuction(Date date);
    void updateAuction(String state);

    @Query("")
    void updateAuction(Long itemId, Double itemStartPrice);
    void updateAuction(Double currentPrice);
    void updateAuction(Double itemFinalPrice, Long buyerId);

}
