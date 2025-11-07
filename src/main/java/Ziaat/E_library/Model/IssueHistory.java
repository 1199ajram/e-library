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

    private Boolean returned = false;

    private String notes;

    private String issuedBy;
    private String returnedBy;
}