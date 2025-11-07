package Ziaat.E_library.Dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CategoryResponse {
    private UUID id;
    private String name;
    private Boolean isActive;
}

