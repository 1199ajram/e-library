package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import Ziaat.E_library.Dto.IssueHistory.MemberDTO;  // Add this import

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCopyResponse {
    private String copyId;
    private String bookId;
    private String barcode;
    private String location;
    private String copyType;
    private String status;
    private String currentBorrower;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private MemberDTO memberDetails;

}