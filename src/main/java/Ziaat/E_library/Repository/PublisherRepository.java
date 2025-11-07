package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PublisherRepository extends JpaRepository<Publisher, UUID> {
    List<Publisher> findByIsActiveTrue();

    // Custom query for more complex search
    @Query("SELECT a FROM Publisher a WHERE " +
            "LOWER(a.firstname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.contact) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.country) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.website) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.lastname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Publisher> searchPublisher(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Get all with pagination (already available from JpaRepository)
    Page<Publisher> findAll(Pageable pageable);
}

