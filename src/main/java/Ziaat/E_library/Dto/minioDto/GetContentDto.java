package Ziaat.E_library.Dto.minioDto;

import lombok.Data;

@Data
public class GetContentDto {
    private String bucketName;
    private String attachmentUrl;
}
