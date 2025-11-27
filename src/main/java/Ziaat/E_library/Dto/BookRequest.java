package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String publishedDate;

    private String coverImageUrl;
    private String coverImageName;

    private String attachmentUrl;
    private String attachmentName;

    private String edition;
    private String placeOfPublisher;
    private String classificationNo;

    // IDs from payload
    private String categoryId;
    private String publisherId;
    private String levelId;
    private String programId;

    // Authors list
    private List<String> authorIds;
}
