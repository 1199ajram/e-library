package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {

    private String bookId;
    private String title;
    private String isbn;
    private String description;
    private String language;
    private int pageCount;
    private LocalDate publishedDate;
    private String coverImageUrl;
    private String attachmentUrl;
    private int rating;
    private String author; // optional single author name
    private int pages;
    private int publishYear;

    private PublisherDTO publisher;
    private CategoryDTO category;
    private List<AuthorDTO> authors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PublisherDTO {
        private String publisherId;
        private String name; // optional
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDTO {
        private String categoryId;
        private String name; // optional
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthorDTO {
        private String authorId;
        private String name; // optional
    }
}
