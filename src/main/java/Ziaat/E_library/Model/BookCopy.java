package Ziaat.E_library.Model;

import Ziaat.E_library.Enumerated.CopyStatus;
import Ziaat.E_library.Enumerated.CopyType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookCopy {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "book_copy_id", updatable = false, nullable = false)
    private UUID copyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @JsonBackReference(value = "book-bookCopy")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Books book;
    @Enumerated(EnumType.STRING)
    private CopyType copyType;
    @Column(unique = true, nullable = false)
    private String barcode;
    @Column(nullable = false)
    private String location;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status = CopyStatus.AVAILABLE;
    private String currentBorrower;
    private LocalDateTime dueDate;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
