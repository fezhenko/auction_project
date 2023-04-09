package org.example.auction.repository;

import org.example.auction.model.Auction;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface AuctionRepository extends Repository<Auction, Long> {

    @Query("select a.* from auctions a;")
    List<Auction> findAllAuctions();

    @Query("select a.* " +
            "from auctions a " +
            "where a.auction_id = :id;")
    Auction findAuctionById(@Param("id") Long id);

    @Query("update auctions set current_price = :currentPrice where auction_id = :id;")
    @Modifying
    void updateAuctionPrice(@Param("id") Long id, @Param("currentPrice") Double currentPrice);

    @Modifying
    @Query("update auctions set final_price = :finalPrice where auction_id = :id")
    void updateAuctionFinalPrice(@Param("finalPrice") Double finalPrice, @Param("id") Long id);

    @Query("update auctions set last_updated = now() where auction_id = :id")
    @Modifying
    void updateLastUpdatedTime(@Param("id") Long id);

    @Query("update auctions set auction_date = :date where auction_id = :id")
    @Modifying
    void updateAuctionDate(@Param("id") Long id, @Param("date") Timestamp newAuctionDate);

    @Query("delete from auctions where auction_id = :id;")
    @Modifying
    void deleteAuction(@Param("id") Long id);

    @Query("update auctions set auction_state = :status where auction_id = :id")
    @Modifying
    void updateAuctionState(@Param("id") Long id, @Param("status") String status);

    @Query("update auctions set item_id = :itemId where auction_id = :id")
    @Modifying
    void updateAuctionItem(@Param("id") Long id, @Param("itemId") Long itemId);

    @Modifying
    @Query("update auctions set auction_state = :state where auction_id = :id")
    void updateAuctionStateBySchedule(@Param("state") String state, @Param("id") Long id);

    @Modifying
    @Query("update auctions set current_price = :amount, buyer_id = :buyerId where auction_id = :auctionId")
    void updateBuyerIdForAuction(@Param("amount") Double amount, @Param("buyerId") Long buyerId,
                                 @Param("auctionId") Long auctionId);

    @Query("select email " +
            "from auctions a " +
            "         join buyers b on a.buyer_id = b.id " +
            "where a.auction_id = :id;")
    String findBuyerEmailByAuctionId(@Param("id") Long id);

    @Query("select email " +
            "from auctions a " +
            "         join sellers s on a.seller_id = s.id " +
            "where a.auction_id = :id;")
    String findSellerEmailByAuctionId(@Param("id") Long id);

    @Modifying
    @Query("update auctions " +
            "set is_payed = true " +
            "where auction_id = :auctionId;")
    void updateIsPayedToTrue(@Param("auctionId") Long auctionId);
}
