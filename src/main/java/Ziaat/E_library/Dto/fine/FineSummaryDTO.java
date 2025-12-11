package Ziaat.E_library.Dto.fine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  FineSummaryDTO {
    private Double totalUnpaid;
    private Double totalPaid;
    private Double totalWaived;
    private Double totalFines;
    private Integer unpaidCount;
    private Integer paidCount;
    private Integer waivedCount;
}