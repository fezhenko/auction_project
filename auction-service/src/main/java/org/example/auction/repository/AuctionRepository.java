package org.example.auction.repository;

import org.example.auction.model.Auction;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuctionRepository extends Repository<Auction, Long> {

    @Query("select a.*, i.description " +
            "from auctions a " +
            "left join items i on a.auction_id = i.auction_id;")
    List<Auction> findAllAuctions();

    @Query("select a.*, i.description " +
            "from auctions a " +
            "left join items i on a.auction_id = i.auction_id " +
            "where a.auction_id = :id;")
    Auction findAuctionById(@Param("id") Long id);

    @Query("update auctions set current_price = :currentPrice where auction_id = :id;")
    @Modifying
    void updateAuctionPrice(@Param("id") Long id, @Param("currentPrice") Double currentPrice);

    @Query("delete from auctions where auction_id = :id;")
    @Modifying
    void deleteAuction(Long id);

//    void updateAuction(Date date);
//    void updateAuction(String state);
//    void updateAuction(Long itemId, Double itemStartPrice);
//    void updateAuction(Double currentPrice);
//    void updateAuction(Double itemFinalPrice, Long buyerId);

}
