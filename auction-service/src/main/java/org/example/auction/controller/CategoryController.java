package org.example.auction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.auction.converter.CategoryConverter;
import org.example.auction.dto.category.CategoryDto;
import org.example.auction.dto.category.UpdateCategoryDto;
import org.example.auction.dto.category.UpdateCategoryResultDto;
import org.example.auction.model.Category;
import org.example.auction.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Categories")
@RequestMapping("/api/v1/categories")
@RestController
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryConverter categoryConverter;

    @Operation(summary = "find all categories")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "categories have been found"),
                    @ApiResponse(responseCode = "204", description = "categories don't exist")
            }
    )
    @GetMapping
    private ResponseEntity<List<CategoryDto>> findAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categoryConverter.toDto(categories));
    }

    @Operation(summary = "find a specific category")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "category have been found"),
                    @ApiResponse(responseCode = "204", description = "category doesn't exist")
            }
    )
    @GetMapping("/{categoryId}")
    private ResponseEntity<CategoryDto> findCategoryById(@PathVariable("categoryId") Long id) {
        Category category = categoryService.findCategoryById(id);
        if (category == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categoryConverter.toDto(category));
    }

    @Operation(summary = "create a new category")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "category has been created"),
                    @ApiResponse(responseCode = "400", description = "invalid body")
            }
    )
    @PostMapping
    private ResponseEntity<UpdateCategoryResultDto> createCategory(@RequestBody @Valid UpdateCategoryDto createCategory) {
        UpdateCategoryResultDto result = categoryService.createCategory(createCategory.getName());
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "update a specific category")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "category has been updated"),
                    @ApiResponse(responseCode = "400", description = "invalid body")
            }
    )
    @PatchMapping("/{categoryId}")
    private ResponseEntity<UpdateCategoryResultDto> updateCategory(@PathVariable("categoryId") Long id,
            @RequestBody @Valid UpdateCategoryDto updateCategory) {
        UpdateCategoryResultDto result = categoryService.updateCategory(id, updateCategory.getName());
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "delete a specific category")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "202", description = "category has been deleted"),
                    @ApiResponse(responseCode = "400", description = "invalid body")
            }
    )
    @DeleteMapping("/{categoryId}")
    private ResponseEntity<UpdateCategoryResultDto> updateCategory(@PathVariable("categoryId") Long id) {
        UpdateCategoryResultDto result = categoryService.deleteCategory(id);
        if (result.getMessage() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.badRequest().body(result);
    }
}
