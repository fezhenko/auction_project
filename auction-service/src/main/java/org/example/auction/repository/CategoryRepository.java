package org.example.auction.repository;

import org.example.auction.model.Category;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends Repository<Category, Long> {
    @Query("select * from categories where id = :id")
    Category findCategoryById(@Param("id") Long itemCategory);
}
