package Ziaat.E_library.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "members")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID memberId;

    @Column(unique = true, nullable = false)
    private String membershipNumber;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    private String address;

    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private LocalDate membershipStartDate;

    private LocalDate membershipEndDate;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType = MembershipType.REGULAR;

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE;

    private Integer maxBooksAllowed = 5;

    private Double fineBalance = 0.0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    public enum MembershipType {
        STUDENT,
        FACULTY,
        REGULAR,
        PREMIUM
    }

    public enum MemberStatus {
        ACTIVE,
        SUSPENDED,
        EXPIRED,
        BLOCKED
    }
}