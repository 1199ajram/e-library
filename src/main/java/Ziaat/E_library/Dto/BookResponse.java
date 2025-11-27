package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    private UUID levelId;

    private String levelName; // optional

    private String categoryName;
    private UUID categoryId;

    private String programName;
    private UUID programId;
    private List<BookCopyDTO> bookCopies;


    private String edition;
    private String placeOfPublisher;
    private String classificationNo;
    private String publisherId;

    private String publisherName;
    private LevelDto level;
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
    public static class LevelDto {
        private UUID levelId;

        private String name; // optional
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthorDTO {
        private String authorId;
        private String name; // optional
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookCopyDTO {
        private String copyId;
        private String barcode;
        private String location;
        private String copyType;
        private String status;
        private String currentBorrower;
        private LocalDateTime dueDate;
    }
}
