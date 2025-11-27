package Ziaat.E_library.Dto;

import lombok.Data;

import java.util.UUID;
@Data
public class ProgramRequestDto {
    private String programName;
    private String programCode;
    private UUID categoryId;
    private Boolean isActive;

}
