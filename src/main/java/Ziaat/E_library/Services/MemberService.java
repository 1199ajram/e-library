package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.MemberRequest;
import Ziaat.E_library.Dto.MemberResponse;
import Ziaat.E_library.Model.IssueHistory;
import Ziaat.E_library.Model.Member;
import Ziaat.E_library.Repository.IssueHistoryRepository;
import Ziaat.E_library.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final IssueHistoryRepository issueHistoryRepository;

    public MemberResponse createMember(MemberRequest request) {
        Member member = new Member();
        member.setMembershipNumber(generateMembershipNumber());
        member.setFirstname(request.getFirstname());
        member.setLastname(request.getLastname());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setMembershipStartDate(LocalDate.now());
        member.setMembershipEndDate(LocalDate.now().plusYears(1));
        member.setMembershipType(Member.MembershipType.valueOf(request.getMembershipType()));
        member.setMaxBooksAllowed(request.getMaxBooksAllowed() != null ? request.getMaxBooksAllowed() : 5);
        member.setStatus(Member.MemberStatus.ACTIVE);
        member.setIsLibrarian(false);

        return mapToResponse(memberRepository.save(member));
    }

    public List<MemberResponse> getAllMember() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MemberResponse> searchMembers(String search) {
        if (search == null || search.isBlank()) {
            return memberRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return memberRepository.searchMembers(search)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<MemberResponse> getAllMembers(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Member> memberPage;
        if (search != null && !search.trim().isEmpty()) {
            memberPage = memberRepository.searchMembers(search, pageable);
        } else {
            memberPage = memberRepository.findAll(pageable);
        }

        List<MemberResponse> responses = memberPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, memberPage.getTotalElements());
    }

    public MemberResponse getMemberById(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return mapToResponse(member);
    }

    public MemberResponse updateMember(UUID memberId, MemberRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setMembershipType(Member.MembershipType.valueOf(request.getMembershipType()));
        member.setFirstname(request.getFirstname());
        member.setLastname(request.getLastname());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setMaxBooksAllowed(request.getMaxBooksAllowed());

        return mapToResponse(memberRepository.save(member));
    }

//    public MemberResponse toggleLibrarianStatus(UUID memberId) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new RuntimeException("Member not found"));
//        // Toggle: if true → false, if false → true
//        member.setIsLibrarian(!member.getIsLibrarian());
//
//        Member savedMember = memberRepository.save(member);
//        return mapToResponse(savedMember);
//    }

    public MemberResponse toggleLibrarianStatus(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Safely treat NULL as false
        boolean currentValue = Boolean.TRUE.equals(member.getIsLibrarian());

        // Toggle
        member.setIsLibrarian(!currentValue);

        Member saved = memberRepository.save(member);
        return mapToResponse(saved);
    }





    public void suspendMember(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setStatus(Member.MemberStatus.SUSPENDED);
        memberRepository.save(member);
    }

    public void activateMember(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        member.setStatus(Member.MemberStatus.ACTIVE);
        memberRepository.save(member);
    }

    public void deleteMember(UUID memberId) {
        memberRepository.deleteById(memberId);
    }

    private String generateMembershipNumber() {
        return "MEM" + System.currentTimeMillis();
    }

    private MemberResponse mapToResponse(Member member) {
        MemberResponse response = new MemberResponse();
        response.setMemberId(member.getMemberId().toString());
        response.setMembershipNumber(member.getMembershipNumber());
        response.setFirstname(member.getFirstname());
        response.setLastname(member.getLastname());
        response.setEmail(member.getEmail());
        response.setPhone(member.getPhone());
        response.setAddress(member.getAddress());
        response.setDateOfBirth(member.getDateOfBirth());
        response.setMembershipStartDate(member.getMembershipStartDate());
        response.setMembershipEndDate(member.getMembershipEndDate());
        response.setMembershipType(member.getMembershipType().name());
        response.setStatus(member.getStatus().name());
        response.setMaxBooksAllowed(member.getMaxBooksAllowed());
        response.setFineBalance(member.getFineBalance());
        response.setCreatedAt(member.getCreatedAt());
        response.setIsLibrarian(member.getIsLibrarian());
        // Get current borrowed books count
        List<IssueHistory> activeIssues = issueHistoryRepository
                .findByMember_MemberIdAndReturnedFalse(member.getMemberId());
        response.setCurrentBorrowedBooks(activeIssues.size());

        return response;
    }
}