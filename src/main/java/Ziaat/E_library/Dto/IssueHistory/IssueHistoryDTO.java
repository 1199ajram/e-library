package Ziaat.E_library.Dto.IssueHistory;

import Ziaat.E_library.Model.Books;
import Ziaat.E_library.Model.IssueHistory;
import Ziaat.E_library.Model.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueHistoryDTO {

    private UUID issueId;
    private UUID bookId;
    private String bookTitle;

    private String memberName;
    private UUID memberId;

    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BookDTO book;
    private MemberDTO member;
    private Boolean returned;
    private String notes;
    private String issuedBy;
    private String returnedBy;

    private Integer renewalCount;
    private Integer maxRenewals;
    private Double fineAmount;

    private Boolean canRenew;
    private Boolean isOverdue;

    public static IssueHistoryDTO fromEntity(IssueHistory issue) {
        IssueHistoryDTO dto = new IssueHistoryDTO();

        dto.setIssueId(issue.getIssueId());

        // ✔ Only send book fields you need
        if (issue.getBook() != null) {
            dto.setBookId(issue.getBook().getBookId());
            dto.setBookTitle(issue.getBook().getTitle());
        }

        // ✅ Convert entities to DTOs
        dto.setBook(BookDTO.fromEntity(issue.getBook()));
        dto.setMember(MemberDTO.fromEntity(issue.getMember()));

        dto.setMemberName(issue.getMemberName());
        if (issue.getMember() != null) {
            dto.setMemberId(issue.getMember().getMemberId());
        }

        dto.setIssueDate(issue.getIssueDate());
        dto.setDueDate(issue.getDueDate());
        dto.setReturnDate(issue.getReturnDate());
        dto.setReturned(issue.getReturned());

        dto.setNotes(issue.getNotes());
        dto.setIssuedBy(issue.getIssuedBy());
        dto.setReturnedBy(issue.getReturnedBy());
        dto.setRenewalCount(issue.getRenewalCount());
        dto.setMaxRenewals(issue.getMaxRenewals());
        dto.setFineAmount(issue.getFineAmount());

        dto.setCanRenew(issue.canRenew());
        dto.setIsOverdue(issue.isOverdue());

        return dto;
    }
}
