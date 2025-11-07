package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    // Search by firstname or lastname with pagination
    Page<Author> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
            String firstname,
            String lastname,
            Pageable pageable
    );

    List<Author> findByIsActiveTrue();

    // Custom query for more complex search
    @Query("SELECT a FROM Author a WHERE " +
            "LOWER(a.firstname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.lastname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Author> searchAuthors(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Get all with pagination (already available from JpaRepository)
    Page<Author> findAll(Pageable pageable);
}

