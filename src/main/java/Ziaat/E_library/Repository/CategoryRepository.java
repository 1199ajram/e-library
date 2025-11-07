package Ziaat.E_library.Repository;

import Ziaat.E_library.Dto.CategoryResponse;
import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // Custom query for more complex search
    @Query("SELECT a FROM Category a WHERE " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Category> searchCategories(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Get all with pagination (already available from JpaRepository)
    Page<Category> findAll(Pageable pageable);

    List<Category> findByIsActiveTrue();
}

