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
    @Query("insert into buyers (auction_id) " +
            "values (:auctionId);")
    void createBuyer(@Param("auctionId") Long id);

    @Modifying
    @Query("insert into buyers (auction_id, bid_id) " +
            "values (:auctionId, :bidId);")
    void createBuyer(@Param("auctionId") Long id, @Param("bidId") Long bidId);

    @Modifying
    @Query("update buyers " +
            "set bid_id = :bidId " +
            "where id = :id;")
    void updateBuyer(@Param("id") Long id, @Param("bidId") Long bidId);

    @Modifying
    @Query("delete " +
            "from buyers " +
            "where id = :id;")
    void deleteBuyer(@Param("id") Long id);

}
