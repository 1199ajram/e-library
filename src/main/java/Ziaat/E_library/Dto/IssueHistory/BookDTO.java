package Ziaat.E_library.Dto.IssueHistory;

import Ziaat.E_library.Model.Books;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private UUID bookId;
    private String title;
    private String authors;  // ✅ Keep as String
    private String isbn;
    private String category;

    public static BookDTO fromEntity(Books book) {
        if (book == null) return null;

        BookDTO dto = new BookDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());

        // ✅ Join multiple authors with comma
        if (book.getBooksAuthors() != null && !book.getBooksAuthors().isEmpty()) {
            dto.setAuthors(book.getBooksAuthors().stream()
                    .map(bookAuthor -> bookAuthor.getAuthor().getFirstname() + " " + bookAuthor.getAuthor().getLastname())
                    .collect(Collectors.joining(", ")));
        }

        dto.setIsbn(book.getIsbn());

        // ✅ Handle category safely
        if (book.getLevel() != null) {
            dto.setCategory(book.getLevel().getLevelName());
        }

        return dto;
    }
}