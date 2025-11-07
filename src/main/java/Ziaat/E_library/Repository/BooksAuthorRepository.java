package Ziaat.E_library.Repository;

import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.BooksAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BooksAuthorRepository extends JpaRepository<BooksAuthor, UUID> {
    List<BooksAuthor> findByIsActiveTrue();

    List<BooksAuthor> findByBookAuthorId(UUID bookAuthorId);
    List<BooksAuthor> findByAuthor(Author author);
}

