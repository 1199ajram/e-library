package Ziaat.E_library.Repository;

import Ziaat.E_library.Enumerated.CopyStatus;
import Ziaat.E_library.Model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {
    List<BookCopy> findByStatus(CopyStatus status);
//    List<BookCopy> findByBookCopyId(UUID bookCopyId);

    List<BookCopy> findByBook_BookId(UUID bookId);
    Optional<BookCopy> findByBarcode(String barcode);
    Long countByBook_BookIdAndStatus(UUID bookId, CopyStatus status);
}

