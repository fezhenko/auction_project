package org.example.auction.converter;

import org.example.auction.dto.category.CategoryDto;
import org.example.auction.model.Category;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper
public interface CategoryConverter {
    List<CategoryDto> toDto(List<Category> categories);
    CategoryDto toDto(Category category);
}
