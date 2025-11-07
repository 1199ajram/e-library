package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.FineRequest;
import Ziaat.E_library.Model.Fine;
import Ziaat.E_library.Model.IssueHistory;
import Ziaat.E_library.Model.Member;
import Ziaat.E_library.Repository.FineRepository;
import Ziaat.E_library.Repository.IssueHistoryRepository;
import Ziaat.E_library.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FineService {

    private final FineRepository fineRepository;
    private final MemberRepository memberRepository;
    private final IssueHistoryRepository issueHistoryRepository;

    private static final Double FINE_PER_DAY = 1.0;

    public Fine createFine(FineRequest request) {
        Member member = memberRepository.findById(UUID.fromString(request.getMemberId()))
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Fine fine = new Fine();
        fine.setMember(member);
        fine.setAmount(request.getAmount());
        fine.setReason(request.getReason());
        fine.setStatus(Fine.FineStatus.UNPAID);
        fine.setCreatedAt(LocalDateTime.now());

        if (request.getIssueId() != null) {
            IssueHistory issue = issueHistoryRepository.findById(UUID.fromString(request.getIssueId()))
                    .orElseThrow(() -> new RuntimeException("Issue not found"));
            fine.setIssue(issue);
        }

        Fine savedFine = fineRepository.save(fine);

        // Update member fine balance
        updateMemberFineBalance(member);

        return savedFine;
    }

    public void calculateOverdueFines() {
        List<IssueHistory> overdueIssues = issueHistoryRepository.findOverdueIssues();

        for (IssueHistory issue : overdueIssues) {
            if (issue.getDueDate() != null && issue.getReturnDate() == null) {
                long daysOverdue = ChronoUnit.DAYS.between(
                        issue.getDueDate().toLocalDate(),
                        LocalDateTime.now().toLocalDate()
                );

                if (daysOverdue > 0) {
                    Double fineAmount = daysOverdue * FINE_PER_DAY;

                    // Check if fine already exists for this issue
                    List<Fine> existingFines = fineRepository.findByMember_MemberIdOrderByCreatedAtDesc(
                            issue.getMember().getMemberId()
                    );

                    boolean fineExists = existingFines.stream()
                            .anyMatch(f -> f.getIssue() != null &&
                                    f.getIssue().getIssueId().equals(issue.getIssueId()));

                    if (!fineExists) {
                        FineRequest request = new FineRequest();
                        request.setMemberId(issue.getMember().getMemberId().toString());
                        request.setIssueId(issue.getIssueId().toString());
                        request.setAmount(fineAmount);
                        request.setReason("Overdue fine: " + daysOverdue + " days");
                        createFine(request);
                    }
                }
            }
        }
    }

    public List<Fine> getFinesByMember(UUID memberId) {
        return fineRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId);
    }

    public Fine payFine(UUID fineId, String paymentMethod) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        fine.setStatus(Fine.FineStatus.PAID);
        fine.setPaidAt(LocalDateTime.now());
        fine.setPaymentMethod(paymentMethod);

        Fine paidFine = fineRepository.save(fine);

        // Update member fine balance
        updateMemberFineBalance(fine.getMember());

        return paidFine;
    }

    public Fine waiveFine(UUID fineId, String notes) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        fine.setStatus(Fine.FineStatus.WAIVED);
        fine.setNotes(notes);

        Fine waivedFine = fineRepository.save(fine);

        // Update member fine balance
        updateMemberFineBalance(fine.getMember());

        return waivedFine;
    }

    private void updateMemberFineBalance(Member member) {
        Double totalUnpaid = fineRepository.getTotalUnpaidFinesByMember(member.getMemberId());
        member.setFineBalance(totalUnpaid != null ? totalUnpaid : 0.0);
        memberRepository.save(member);
    }
}