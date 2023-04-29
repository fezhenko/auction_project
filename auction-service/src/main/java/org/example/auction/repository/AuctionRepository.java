package org.example.auction.repository;

import java.sql.Timestamp;
import java.util.List;

import org.example.auction.model.Auction;
import org.example.auction.scheduler.AuctionStatus;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AuctionRepository extends CrudRepository<Auction, Long> {

    Auction findAuctionByAuctionId(Long id);

    @Modifying
    @Query(value = "insert into auctions (seller_id) values (:sellerId);")
    void createAuction(@Param("sellerId") Long sellerId);

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

    @Modifying
    void deleteAuctionByAuctionId(Long id);

    @Query("update auctions set auction_state = :status where auction_id = :id")
    @Modifying
    void updateAuctionState(@Param("id") Long id, @Param("status") String status);

    @Query("update auctions set item_id = :itemId where auction_id = :id")
    @Modifying
    void updateAuctionItem(@Param("id") Long id, @Param("itemId") Long itemId);

    @Modifying
    @Query("update auctions set auction_state = :state where auction_id = :id")
    void updateAuctionStateBySchedule(@Param("state") AuctionStatus auctionStatus, @Param("id") Long id);

    @Modifying
    @Query("update auctions set current_price = :amount, buyer_id = :buyerId where auction_id = :auctionId")
    void updateBuyerIdForAuction(@Param("amount") Double amount, @Param("buyerId") Long buyerId,
                                 @Param("auctionId") Long auctionId);

    @Modifying
    @Query("update auctions " +
        "set is_payed = true " +
        "where auction_id = :auctionId;")
    void updateIsPayedToTrue(@Param("auctionId") Long auctionId);

    @Modifying
    @Query("update auctions " +
        "set item_id     = :itemId, " +
        "    start_price = :startPrice, " +
        "    minimal_bid = :minBid " +
        "where auction_id = :auctionId;")
    void addItemToAuction(@Param("auctionId") Long auctionId, @Param("itemId") Long itemId,
                          @Param("startPrice") Double startPrice, @Param("minBid") Double minimalBid);

    @Query("select a.* " +
        "from auctions a " +
        "         join sellers s on a.auction_id = s.auction_id " +
        "where s.email = :sellerEmail;")
    Auction findAuctionBySellerEmail(@Param("sellerEmail") String email);

    List<Auction> findAuctionsBySellerId(@Param("id") Long sellerId);

    @Modifying
    void deleteAuctionBySellerId(Long sellerId);
}
