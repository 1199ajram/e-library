package Ziaat.E_library.Services;


import Ziaat.E_library.Dto.IssueHistory.IssueHistoryDTO;
import Ziaat.E_library.Enumerated.CopyStatus;
import Ziaat.E_library.Model.BookCopy;
import Ziaat.E_library.Model.Books;
import Ziaat.E_library.Model.IssueHistory;
import Ziaat.E_library.Model.Member;
import Ziaat.E_library.Repository.BookCopyRepository;
import Ziaat.E_library.Repository.BookRepository;
import Ziaat.E_library.Repository.IssueHistoryRepository;
import Ziaat.E_library.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IssueHistoryService {

    private final IssueHistoryRepository issueHistoryRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository booksRepository;
    private final MemberRepository memberRepository;

    // Get current issues for a member
    public List<IssueHistoryDTO> getCurrentIssuesByMember(UUID memberId) {
        List<IssueHistory> issues = issueHistoryRepository.findByMemberMemberIdAndReturnedFalse(memberId);

        // Update overdue status
        issues.forEach(issue -> {
            if (issue.isOverdue()) {
                issueHistoryRepository.save(issue);
            }
        });

        return issues.stream()
                .map(IssueHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get borrowing history for a member
    public Page<IssueHistoryDTO> getBorrowingHistory(UUID memberId, Pageable pageable) {
        Page<IssueHistory> issues = issueHistoryRepository.findByMemberMemberId(memberId, pageable);
        return issues.map(IssueHistoryDTO::fromEntity);
    }

    // Issue a book to a member
    public IssueHistoryDTO issueBook(UUID copyId, UUID bookId, UUID memberId, String notes, String issuedBy) {
        // Validate member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Check if member has reached borrowing limit
        long currentIssues = issueHistoryRepository.countCurrentIssuesByMember(memberId);
        if (currentIssues >= member.getMaxBooksAllowed()) {
            throw new RuntimeException("Member has reached maximum borrowing limit");
        }

        // Check if member has outstanding fines
        if (member.getFineBalance() != null && member.getFineBalance() > 50.0) {
            throw new RuntimeException("Member has outstanding fines exceeding limit");
        }

        // Validate book copy
        BookCopy bookCopy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new RuntimeException("Book copy not found"));

        // Validate book
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Check if copy is available
        if (!"AVAILABLE".equals(bookCopy.getStatus())) {
            throw new RuntimeException("Book copy is not available for borrowing");
        }

        // Check if copy is already issued
        if (issueHistoryRepository.existsByBookCopyCopyIdAndReturnedFalse(copyId)) {
            throw new RuntimeException("Book copy is already issued to another member");
        }

        // Create issue record
        IssueHistory issue = new IssueHistory();
        issue.setBookCopy(bookCopy);
        issue.setBook(book);
        issue.setMember(member);
        issue.setMemberName(member.getFirstname() + " " + member.getLastname());
        issue.setIssueDate(LocalDateTime.now());
        issue.setDueDate(LocalDateTime.now().plusDays(14)); // 14 days borrowing period
        issue.setReturned(false);
        issue.setRenewalCount(0);
        issue.setMaxRenewals(2);
        issue.setFineAmount(0.0);
        issue.setNotes(notes);
        issue.setIssuedBy(issuedBy);

        // Update book copy status
        bookCopy.setStatus(CopyStatus.BORROWED);
        bookCopy.setCurrentBorrower(String.valueOf(member.getMemberId()));
        bookCopy.setDueDate(issue.getDueDate());
        bookCopyRepository.save(bookCopy);

        // Save issue
        IssueHistory savedIssue = issueHistoryRepository.save(issue);

        return IssueHistoryDTO.fromEntity(savedIssue);
    }

    // Return a book
    public IssueHistoryDTO returnBook(UUID issueId, String returnedBy) {
        IssueHistory issue = issueHistoryRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue record not found"));

        if (Boolean.TRUE.equals(issue.getReturned())) {
            throw new RuntimeException("Book has already been returned");
        }

        // Set return date
        issue.setReturnDate(LocalDateTime.now());
        issue.setReturned(true);
        issue.setReturnedBy(returnedBy);

        // Calculate fine if overdue
        if (issue.isOverdue()) {
            long daysOverdue = java.time.Duration.between(issue.getDueDate(), LocalDateTime.now()).toDays();
            double fineAmount = daysOverdue * 1.0; // $1 per day
            issue.setFineAmount(fineAmount);

            // Update member's fine balance
            Member member = issue.getMember();
            Double currentFineBalance = member.getFineBalance() != null ? member.getFineBalance() : 0.0;
            member.setFineBalance(currentFineBalance + fineAmount);
            memberRepository.save(member);
        }

        // Update book copy status
        BookCopy bookCopy = issue.getBookCopy();
        bookCopy.setStatus(CopyStatus.BORROWED);
        bookCopy.setCurrentBorrower(null);
        bookCopy.setDueDate(null);
        bookCopyRepository.save(bookCopy);

        // Save issue
        IssueHistory savedIssue = issueHistoryRepository.save(issue);

        return IssueHistoryDTO.fromEntity(savedIssue);
    }

    // Renew a book
    public IssueHistoryDTO renewBook(UUID issueId) {
        IssueHistory issue = issueHistoryRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue record not found"));

        // Check if book can be renewed
        if (!issue.canRenew()) {
            if (issue.isOverdue()) {
                throw new RuntimeException("Cannot renew overdue book. Please return it first.");
            }
            throw new RuntimeException("Maximum renewals reached");
        }

        // Extend due date by 14 days
        issue.setDueDate(issue.getDueDate().plusDays(14));
        issue.setRenewalCount(issue.getRenewalCount() + 1);

        // Update book copy due date
        BookCopy bookCopy = issue.getBookCopy();
        bookCopy.setDueDate(issue.getDueDate());
        bookCopyRepository.save(bookCopy);

        // Save issue
        IssueHistory savedIssue = issueHistoryRepository.save(issue);

        return IssueHistoryDTO.fromEntity(savedIssue);
    }

    // Get all overdue issues
    public List<IssueHistoryDTO> getOverdueIssues() {
        List<IssueHistory> overdueIssues = issueHistoryRepository.findOverdueIssues(LocalDateTime.now());
        return overdueIssues.stream()
                .map(IssueHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get all issues
    public Page<IssueHistoryDTO> getAllIssues(Pageable pageable) {
        Page<IssueHistory> issues = issueHistoryRepository.findAll(pageable);
        return issues.map(IssueHistoryDTO::fromEntity);
    }
}