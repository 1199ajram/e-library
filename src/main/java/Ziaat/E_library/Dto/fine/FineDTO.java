package Ziaat.E_library.Dto.fine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

// FineDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FineDTO {
    private UUID fineId;
    private UUID memberId;
    private String bookTitle;
    private Double amount;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String paymentMethod;
    private String notes;
}