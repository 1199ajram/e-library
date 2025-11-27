package Ziaat.E_library.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "levels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Level {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "level_id", updatable = false, nullable = false)
    private UUID levelId;

    @Column(name = "level_name", nullable = false)
    private String levelName;

    @Column(name = "level_code",nullable = true)
    private String levelCode;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;
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
}
