package Ziaat.E_library.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name ="publishers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Publisher {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "publisher_id",updatable = false, nullable = false)
    private UUID publisherId;

    private String firstname;
    private String lastname;
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(nullable = true)
    private String address;
    @Column(nullable = true)
    private String website;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String contact;

    @Column(nullable = true)
    private String country;

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

