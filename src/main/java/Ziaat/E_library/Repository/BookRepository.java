package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Books;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Books, UUID> {
    Optional<Books> findByIsbn(String isbn);
    List<Books> findByTitleContainingIgnoreCase(String title);
    // Search by title, author, or ISBN with pagination

    @Query("""
    SELECT DISTINCT b FROM Books b
    LEFT JOIN b.booksAuthors ba
    LEFT JOIN b.category c
    WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
       OR LOWER(ba.author.firstname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
       OR LOWER(ba.author.lastname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
       OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
       OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
""")
    Page<Books> searchBooks(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Books b WHERE b.category.categoryId = :categoryId")
    Page<Books> searchBooksByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);


    Page<Books> findByCategory(String category, Pageable pageable);


    List<Books> findByCategory_CategoryId(UUID categoryId);
    List<Books> findByPublisher_PublisherId(UUID publisherId);
}

