package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String membershipType;
    private Integer maxBooksAllowed;
}
