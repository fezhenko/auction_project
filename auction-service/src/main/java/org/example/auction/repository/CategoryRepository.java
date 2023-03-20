package org.example.auction.repository;

import org.example.auction.model.Category;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends Repository<Category, Long> {
    @Query("select * from categories")
    List<Category> findAllCategories();

    @Query("select * from categories where id = :id")
    Category findCategoryById(@Param("id") Long id);

    @Modifying
    @Query("insert into categories (id, name) values (default, :name);")
    void createCategory(@Param("name") String name);

    @Modifying
    @Query("update categories set name = :name where id = :id")
    void updateCategory(@Param("id") Long id, @Param("name") String name);

    @Modifying
    @Query("delete from categories where id = :id")
    void deleteCategoryBy(@Param("id") Long id);
}
