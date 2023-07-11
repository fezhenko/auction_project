package org.example.usersservice.repository;


import java.util.List;

import javax.validation.constraints.NotNull;
import org.example.usersservice.model.Item;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends Repository<Item, Long> {
    @Query("select i.*, cur.currency as currencyValue, cat.name as categoryName from items i " +
            "left join currencies cur on i.currency_id = cur.id " +
            "left join categories cat on i.item_category = cat.id ")
    List<Item> findAllItems();

    @Query("select i.*, cur.currency as currencyValue, cat.name as categoryName from items i " +
            "left join currencies cur on i.currency_id = cur.id " +
            "left join categories cat on i.item_category = cat.id " +
            "where i.id = :id")
    Item findItemById(@Param("id") Long id);

    @Modifying
    @Query("insert into items (price, owner_id) values (:price, :owner_id);")
    void createItem(@Param("owner_id") Long ownerId, @Param("price") Double itemPrice);

    @Modifying
    @Query("insert into items (price, owner_id, description) values (:price, :owner_id, :description);")
    void createItem(@Param("owner_id") Long auctionId, @Param("price") Double itemPrice,
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
    @Query("delete from items where id = :id")
    void deleteItemById(@Param("id") Long id);

    List<Item> findItemByOwnerId(@NotNull Long ownerId);
}
