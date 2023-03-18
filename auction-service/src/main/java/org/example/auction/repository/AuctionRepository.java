package org.example.auction.repository;

import org.example.auction.model.Auction;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuctionRepository extends Repository<Auction, Long> {

    @Query("select a.*, i.description " +
            "from auctions a " +
            "join items i on a.auction_id = i.auction_id;")
    List<Auction> findAllAuctions();

    @Query("select a.*, i.description " +
            "from auctions a " +
            "join items i on a.auction_id = i.auction_id " +
            "where a.auction_id = :id;")
    Auction findAuctionById(@Param("id") Long id);

//    void createAuction(Long sellerId);
//
//    void updateAuction(Date date);
//
//    void updateAuction(String state);
//
//    void updateAuction(Long itemId, Double itemStartPrice);
//
//    void updateAuction(Double currentPrice);
//
//    void updateAuction(Double itemFinalPrice, Long buyerId);

}
