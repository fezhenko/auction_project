package org.example.usersservice.converter;

import org.example.usersservice.dto.category.CategoryDto;
import org.example.usersservice.model.Category;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper
public interface CategoryConverter {
    List<CategoryDto> toDto(List<Category> categories);
    CategoryDto toDto(Category category);
}
