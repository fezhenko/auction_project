package org.example.usersservice.repository;


import org.example.usersservice.model.Item;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends Repository<Item, Long> {
    @Query("select i.*, cur.currency as currencyValue, cat.name as categoryName from items i " +
            "left join currencies cur on i.currency = cur.id " +
            "left join categories cat on i.item_category = cat.id ")
    List<Item> findAllItems();

    @Query("select i.*, cur.currency as currencyValue, cat.name as categoryName from items i " +
            "left join currencies cur on i.currency = cur.id " +
            "left join categories cat on i.item_category = cat.id " +
            "where i.id = :id")
    Item findItemById(@Param("id") Long id);

    @Modifying
    @Query("insert into items (price, auction_id) values (:price, :auctionId);")
    void createItem(@Param("auctionId") Long auctionId, @Param("price") Double itemPrice);

    @Modifying
    @Query("insert into items (price, auction_id, description) values (:price, :auctionId, :description);")
    void createItem(@Param("auctionId") Long auctionId, @Param("price") Double itemPrice,
                    @Param("description") String description);

    @Modifying
    @Query("update items set description = :description, price = :price, item_state = :itemStatus, " +
            "item_category = :categoryId " +
            "where id = :itemId;")
    void updateItem(@Param("description") String description, @Param("price") Double price,
                    @Param("itemStatus") String status, @Param("categoryId") Long categoryId,
                    @Param("itemId") Long itemId);

    @Modifying
    @Query("update items set description = :description, price = :price, item_state = :itemStatus " +
            "where id = :itemId;")
    void updateItem(@Param("description") String description, @Param("price") Double price,
                    @Param("itemStatus") String itemStatus, @Param("itemId") Long id);

    @Modifying
    @Query("delete from items where auction_id = :id")
    void deleteItemByAuctionId(@Param("id") Long id);
}
