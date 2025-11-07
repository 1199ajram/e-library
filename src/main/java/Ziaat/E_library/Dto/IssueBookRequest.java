package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueBookRequest {
    private String copyId;
    private String memberName;
    private String memberId;
    private LocalDateTime dueDate;
    private String notes;
}