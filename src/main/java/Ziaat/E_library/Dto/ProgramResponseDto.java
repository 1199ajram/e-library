package Ziaat.E_library.Dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
public class ProgramResponseDto {
    private UUID programId;
    private String programName;
    private String programCode;
    private List<LevelResponseDto> levels;
    private CategoryResponse category;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;

}
