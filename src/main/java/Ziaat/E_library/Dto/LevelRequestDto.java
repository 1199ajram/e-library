package Ziaat.E_library.Dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.UUID;
@Data
public class LevelRequestDto {
    private String levelName;
    private String levelCode;
    private UUID programId;
    private Boolean isActive;
}
