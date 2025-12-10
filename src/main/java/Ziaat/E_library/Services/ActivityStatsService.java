package Ziaat.E_library.Services;


import Ziaat.E_library.Dto.ActivityStatsDTO;
import Ziaat.E_library.Model.Member;
import Ziaat.E_library.Repository.FineRepository;
import Ziaat.E_library.Repository.IssueHistoryRepository;
import Ziaat.E_library.Repository.MemberRepository;
import Ziaat.E_library.Repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityStatsService {

    private final IssueHistoryRepository borrowingRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final FineRepository fineRepository;

    public ActivityStatsDTO getActivityStats(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Calculate total books borrowed
        Integer totalBooksBorrowed = borrowingRepository
                .countTotalBooksBorrowedByMember(memberId);

        // Calculate overdue books
        Integer overdueBooks = borrowingRepository
                .countOverdueBooksByMember(memberId);

        // Calculate on-time return rate
        Double onTimeReturnRate = calculateOnTimeReturnRate(memberId);

        // Calculate membership years
        Integer membershipYears = calculateMembershipYears(member.getMembershipStartDate());

        // Get active reservations count
        Integer activeReservations = reservationRepository
                .countActiveReservationsByMember(memberId);

        // Calculate total fines
        Double totalFines = fineRepository
                .getTotalUnpaidFinesByMember(memberId);

        return ActivityStatsDTO.builder()
                .totalBooksBorrowed(totalBooksBorrowed != null ? totalBooksBorrowed : 0)
                .onTimeReturnRate(onTimeReturnRate != null ? onTimeReturnRate : 0.0)
                .overdueBooks(overdueBooks != null ? overdueBooks : 0)
                .membershipYears(membershipYears != null ? membershipYears : 0)
                .activeReservations(activeReservations != null ? activeReservations : 0)
                .totalFines(totalFines != null ? totalFines : 0.0)
                .build();
    }

    private Double calculateOnTimeReturnRate(UUID memberId) {
        Integer onTimeReturns = borrowingRepository.countOnTimeReturnsByMember(memberId);
        Integer totalReturns = borrowingRepository.countTotalReturnsByMember(memberId);

        if (totalReturns == null || totalReturns == 0) {
            return 100.0; // No returns yet, assume 100%
        }

        double rate = ((double) onTimeReturns / totalReturns) * 100;
        return Math.round(rate * 100.0) / 100.0; // Round to 2 decimal places
    }

    private Integer calculateMembershipYears(LocalDate membershipStartDate) {
        if (membershipStartDate == null) {
            return 0;
        }

        Period period = Period.between(membershipStartDate, LocalDate.now());
        return period.getYears();
    }
}