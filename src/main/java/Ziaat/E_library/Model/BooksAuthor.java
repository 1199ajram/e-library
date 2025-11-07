package Ziaat.E_library.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "books_author")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BooksAuthor {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "book_author_id",updatable = false, nullable = false)
    private UUID bookAuthorId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id",nullable = true)
    @JsonBackReference(value = "books-booksAuthors")
    @EqualsAndHashCode.Exclude
    @lombok.ToString.Exclude
    private Books books;


    @ManyToOne(optional = true)
    @JoinColumn(name = "author_id", referencedColumnName = "author_id",nullable = true)
    @JsonBackReference(value = "books-booksAuthors")
    @EqualsAndHashCode.Exclude
    @lombok.ToString.Exclude
    private Author author;

    @Column(name = "is_active")
    private Boolean isActive;

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

    // Getters and Setters
}
