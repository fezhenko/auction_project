package org.example.auction.repository;

import java.util.List;

import org.example.auction.model.Bid;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface BidRepository extends Repository<Bid, Long> {
    @Query("select * from bids;")
    List<Bid> findAllBids();

    Bid findBidByBidId(Long bidId);

    @Modifying
    @Query("insert into bids (bid_amount, buyer_id) values (:bidAmount, :id);")
    void createBid(@Param("bidAmount") Double bidAmount, @Param("id") Long buyerId);

    @Modifying
    @Query("update bids set bid_amount = :bidAmount where bid_id = :bidId;")
    void updateBidById(@Param("bidAmount") Double bidAmount, @Param("bidId") Long bidId);

    @Modifying
    void deleteBidByBidId(Long bidId);
}
