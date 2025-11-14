package Ziaat.E_library.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "issue_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Books book;

    @Column(nullable = false)
    private String memberName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    @Column(nullable = false)
    private Boolean returned = false;

    private String notes;

    private String issuedBy;
    private String returnedBy;

    // Add renewal tracking
    @Column(nullable = false)
    private Integer renewalCount = 0;

    @Column(nullable = false)
    private Integer maxRenewals = 2;

    // Add fine tracking
    private Double fineAmount = 0.0;

    // Utility methods
    public boolean isOverdue() {
        return !returned && dueDate != null && LocalDateTime.now().isAfter(dueDate);
    }

    public boolean canRenew() {
        return !returned && renewalCount < maxRenewals && !isOverdue();
    }

    @PrePersist
    protected void onCreate() {
        if (issueDate == null) {
            issueDate = LocalDateTime.now();
        }
        if (dueDate == null) {
            dueDate = issueDate.plusDays(14);
        }
        if (returned == null) {
            returned = false;
        }
        if (renewalCount == null) {
            renewalCount = 0;
        }
        if (maxRenewals == null) {
            maxRenewals = 2;
        }
        if (fineAmount == null) {
            fineAmount = 0.0;
        }
    }
}