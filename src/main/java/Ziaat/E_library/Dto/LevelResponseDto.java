package Ziaat.E_library.Dto;

import Ziaat.E_library.Model.Program;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
public class LevelResponseDto {
    private UUID levelId;
    private String levelName;
    private String levelCode;
    private Program program;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;

}
