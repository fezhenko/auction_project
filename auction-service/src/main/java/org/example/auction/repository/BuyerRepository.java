package org.example.auction.repository;

import org.example.auction.model.Buyer;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuyerRepository extends Repository<Buyer, Long> {
    @Query("")
    List<Buyer> findAll();

    @Query("")
    Buyer findBuyerByAuctionId(@Param("auctionId") Long id);

    @Query("")
    void createBuyer(@Param("auctionId") Long id);

    @Query("")
    void updateBuyer(@Param("auctionId") Long id, @Param("bidId") Long bidId);

    @Query("")
    void deleteBuyer(@Param("auctionId") Long id);

}
