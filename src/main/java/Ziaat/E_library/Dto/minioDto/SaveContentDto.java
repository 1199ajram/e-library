package Ziaat.E_library.Dto.minioDto;

import lombok.Data;

@Data
public class SaveContentDto {
    private String bucketName;
    private FileDto file;
}
