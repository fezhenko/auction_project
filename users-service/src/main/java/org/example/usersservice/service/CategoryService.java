package org.example.usersservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.usersservice.dto.category.UpdateCategoryResultDto;
import org.example.usersservice.model.Category;
import org.example.usersservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAllCategories() {
        return categoryRepository.findAllCategories();
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findCategoryById(id);
    }

    public UpdateCategoryResultDto createCategory(String name) {
        categoryRepository.createCategory(name);
        return UpdateCategoryResultDto.builder().build();
    }

    public UpdateCategoryResultDto updateCategory(Long id, String name) {
        if (categoryRepository.findCategoryById(id) == null) {
            log.error("category with id:'%d' doesn't exist".formatted(id));
            return UpdateCategoryResultDto.builder().message("category with id:'%d' doesn't exist".formatted(id))
                    .build();
        }
        categoryRepository.updateCategory(id, name);
        return UpdateCategoryResultDto.builder().build();
    }

    public UpdateCategoryResultDto deleteCategory(Long id) {
        if (categoryRepository.findCategoryById(id) == null) {
            log.error("category with id:'%d' doesn't exist".formatted(id));
            return UpdateCategoryResultDto.builder().message("category with id:'%d' doesn't exist".formatted(id))
                    .build();
        }
        categoryRepository.deleteCategoryBy(id);
        return UpdateCategoryResultDto.builder().build();
    }
}
