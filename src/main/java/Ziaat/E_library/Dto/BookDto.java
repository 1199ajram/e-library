package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private UUID bookId;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
    private String description;
    private String language;
    private Integer pageCount;
    private String coverImageUrl;
    private String attachmentUrl;
}