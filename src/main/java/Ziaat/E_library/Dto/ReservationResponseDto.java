package Ziaat.E_library.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import Ziaat.E_library.Dto.BookDto;
import Ziaat.E_library.Dto.MemberResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDto {
    private UUID reservationId;

    // Nested objects
    private BookDto book;
    private MemberResponse member;

    // Flattened fields for convenience
    private UUID bookId;
    private UUID memberId;
    private String bookTitle;
    private String bookCoverUrl;
    private String memberName;
    private String memberEmail;
    private String coverImageUrl;

    // Reservation details
    private LocalDateTime reservationDate;
    private LocalDateTime expiryDate;
    private String status;
    private String notes;
}