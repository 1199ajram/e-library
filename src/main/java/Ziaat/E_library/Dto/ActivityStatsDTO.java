package Ziaat.E_library.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatsDTO {
    private Integer totalBooksBorrowed;
    private Double onTimeReturnRate;
    private Integer overdueBooks;
    private Integer membershipYears;
    private Integer activeReservations;
    private Double totalFines;
}