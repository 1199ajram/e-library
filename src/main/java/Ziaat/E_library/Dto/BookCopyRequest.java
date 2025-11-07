package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCopyRequest {
    private String bookId;
    private String barcode;
    private String location;
    private String copyType;
    private String status;
}