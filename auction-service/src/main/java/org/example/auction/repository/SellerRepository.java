package org.example.auction.repository;

import org.example.auction.model.Seller;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SellerRepository extends Repository<Seller, Long> {
    @Query("select * from sellers")
    List<Seller> findAll();

    @Query("select * " +
            "from sellers " +
            "where auction_id = :auctionId;")
    Seller findSellersByAuctionId(@Param("auctionId") Long auctionId);

    @Query("select * " +
            "from sellers " +
            "where id = :id;")
    Seller findSellerById(@Param("id") Long id);

    @Query("insert into sellers (auction_id) " +
            "values (:auctionId);")
    void createSeller(@Param("auctionId") Long auctionId);

    @Query("delete " +
            "from sellers " +
            "where auction_id = :auctionId;")
    void deleteSellerData(@Param("auctionId") Long auctionId);

}
