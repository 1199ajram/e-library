package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.CategoryRequest;
import Ziaat.E_library.Dto.CategoryResponse;
import Ziaat.E_library.Model.Category;
import Ziaat.E_library.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setIsActive(request.getIsActive());
        return mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(request.getName());
        category.setIsActive(request.getIsActive());
        return mapToResponse(categoryRepository.save(category));
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    public CategoryResponse getCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToResponse(category);
    }

    public Page<Category> getAllCategories(int page, int size, String sortBy, String sortDir, String search) {
        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return categoryRepository.searchCategories(search, pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    public List<CategoryResponse> getActive() {
        return categoryRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setName(category.getName());
        response.setIsActive(category.getIsActive());
        return response;
    }
}

