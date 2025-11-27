package Ziaat.E_library.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Books {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "book_id", updatable = false, nullable = false)
    private UUID bookId;

    private String title;

    @Column(unique = true)
    private String isbn;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "book")
    @JsonManagedReference(value = "book-bookCopy")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<BookCopy> bookCopies;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "books")
    @JsonManagedReference(value = "booksAuthors-books")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private List<BooksAuthor> booksAuthors;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    private LocalDate publishedDate;
    private String description;
    private String language;
    private int pageCount;
    private String coverImageUrl;
    @Column(name = "attachment_url", nullable = true)
    private String attachmentUrl;


    private String edition;
    private String placeOfPublisher;
    private String classificationNo;

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
