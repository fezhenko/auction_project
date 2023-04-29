package org.example.auction.repository;

import java.util.List;

import org.example.auction.model.Buyer;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface BuyerRepository extends Repository<Buyer, Long> {
    @Query("select * from buyers")
    List<Buyer> findAll();

    Buyer findBuyerById(Long id);

    Buyer findBuyerByAuctionId(Long auctionId);

    @Modifying
    @Query("insert into buyers (email, auction_id) " +
            "values (:email, :auctionId);")
    void createBuyer(@Param("email") String email, @Param("auctionId") Long id);

    @Modifying
    void deleteBuyerById(Long id);

    Buyer findBuyerByEmail(String email);
}
