package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {

    private String title;
    private String isbn;
    private String description;
    private String language;
    private int pageCount;
    private String publishedDate; // format: "YYYY-MM-DD"
    private String coverImageUrl;
    private String coverImageName;
    private String  attachmentUrl;
    private String attachmentName;
    private int rating;
    private String author; // single author name if needed
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
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDTO {
        private String categoryId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthorDTO {
        private String authorId;
    }
}

