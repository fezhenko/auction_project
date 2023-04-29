package org.example.auction.repository;

import java.util.List;

import org.example.auction.model.Seller;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface SellerRepository extends Repository<Seller, Long> {
    @Query("select * from sellers")
    List<Seller> findAll();

    Seller findSellerByAuctionId(Long auctionId);

    Seller findSellersBySellerId(Long id);

    @Query("insert into sellers (email) values (:email)")
    @Modifying
    void createSeller(@Param("email") String email);

    @Modifying
    void deleteSellerBySellerId(Long auctionId);

    Seller findSellerByEmail(String email);
}
