package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Level;
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
public interface LevelRepository extends JpaRepository<Level, UUID> {
    List<Level> findByIsActiveTrue();
    @Query("SELECT a FROM Level a WHERE " +
            "LOWER(a.levelName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.levelCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Level> searchLevel(@Param("searchTerm") String searchTerm, Pageable pageable);


    List<Level> findByProgram_ProgramIdAndIsActiveTrue(UUID programId);

}
