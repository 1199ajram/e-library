package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private String memberId;
    private String membershipNumber;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private String membershipType;
    private String status;
    private Integer maxBooksAllowed;
    private Double fineBalance;
    private Integer currentBorrowedBooks;
    private LocalDateTime createdAt;
}