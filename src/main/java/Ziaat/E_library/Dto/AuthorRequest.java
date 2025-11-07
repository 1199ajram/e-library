package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequest {
    private String firstname;
    private String lastname;
    private Boolean isActive;
}

