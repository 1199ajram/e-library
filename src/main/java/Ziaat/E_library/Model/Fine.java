package Ziaat.E_library.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fines")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID fineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private IssueHistory issue;

    @Column(nullable = false)
    private Double amount;

    private String reason;

    @Enumerated(EnumType.STRING)
    private FineStatus status = FineStatus.UNPAID;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private String paymentMethod;

    private String notes;

    public enum FineStatus {
        UNPAID,
        PAID,
        WAIVED
    }
}