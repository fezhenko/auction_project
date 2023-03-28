package org.example.auction.repository;

import org.example.auction.model.Buyer;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuyerRepository extends Repository<Buyer, Long> {
    @Query("select * from buyers")
    List<Buyer> findAll();

    @Query("select b.id, b.bid_id, b.auction_id, b.created_at " +
            "from buyers b " +
            "where b.id = :buyerId;")
    Buyer findBuyerById(@Param("buyerId") Long id);

    @Modifying
    @Query("insert into buyers (email, auction_id) " +
            "values (:email, :auctionId);")
    void createBuyer(@Param("email") String email, @Param("auctionId") Long id);

    @Modifying
    @Query("delete " +
            "from buyers " +
            "where id = :id;")
    void deleteBuyer(@Param("id") Long id);

    @Query("select id from buyers where email = :email")
    Long findBuyerByEmail(@Param("email") String email);

    @Query("select auction_id from buyers where id = :buyerId")
    Long findAuctionByBuyerId(@Param("buyerId") Long buyerId);
}
