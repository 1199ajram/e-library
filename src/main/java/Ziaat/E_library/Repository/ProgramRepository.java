package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Category;
import Ziaat.E_library.Model.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    List<Program> findByIsActiveTrue();
    @Query("SELECT a FROM Program a WHERE " +
            "LOWER(a.programName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.programCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Program> searchProgram(@Param("searchTerm") String searchTerm, Pageable pageable);

    List<Program> findByCategory_CategoryIdAndIsActiveTrue(UUID categoryId);

}
