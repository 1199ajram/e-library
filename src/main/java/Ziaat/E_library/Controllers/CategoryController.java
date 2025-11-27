package Ziaat.E_library.Controllers;


import Ziaat.E_library.Dto.CategoryRequest;
import Ziaat.E_library.Dto.CategoryResponse;
import Ziaat.E_library.Model.Category;
import Ziaat.E_library.Model.Publisher;
import Ziaat.E_library.Services.CategoryService;
import Ziaat.E_library.Utils.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories Management", description = "APIs for managing categories in the library system")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @Operation(
            summary = "Create a new categories",
            description = "Creates a new categories in the library system"
    )
    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Parameter(description = "categories details", required = true)
            @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }


    @Operation(
            summary = "Update categories",
            description = "Updates an existing categories information"
    )
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @Parameter(description = "categories ID", required = true, example = "1")
            @PathVariable UUID id,
            @Parameter(description = "Updated categories details", required = true)
            @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get categories by ID",
            description = "Retrieves a specific categories by their ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> get(
            @Parameter(description = "categories ID", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @Operation(
            summary = "Get all categories with pagination and search",
            description = "Retrieves a paginated list of categories with optional search functionality"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved paginated list of categories",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PageResponse<Category>> getAllCategories(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "name")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir,

            @Parameter(description = "Search term to filter authors by name", example = "Book")
            @RequestParam(required = false) String search
    ) {
        Page<Category> categoriesPage = categoryService.getAllCategories(page, size, sortBy, sortDir, search);

        PageResponse<Category> response = new PageResponse<>();
        response.setContent(categoriesPage.getContent());
        response.setPageNumber(categoriesPage.getNumber());
        response.setPageSize(categoriesPage.getSize());
        response.setTotalElements(categoriesPage.getTotalElements());
        response.setTotalPages(categoriesPage.getTotalPages());
        response.setLast(categoriesPage.isLast());
        response.setFirst(categoriesPage.isFirst());
        response.setHasNext(categoriesPage.hasNext());
        response.setHasPrevious(categoriesPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CategoryResponse>> getActive() {
        return ResponseEntity.ok(categoryService.getActive());
    }
}

