package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.*;
import Ziaat.E_library.Dto.IssueHistory.MemberDTO;
import Ziaat.E_library.Enumerated.CopyStatus;
import Ziaat.E_library.Enumerated.CopyType;
import Ziaat.E_library.Model.*;
import Ziaat.E_library.Repository.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final IssueHistoryRepository issueHistoryRepository;
    private final MemberRepository memberRepository;

    // Create Book Copy
    public BookCopyResponse createBookCopy(BookCopyRequest request) {
        Books book = bookRepository.findById(UUID.fromString(request.getBookId()))
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setBarcode(request.getBarcode());
        bookCopy.setLocation(request.getLocation());
        bookCopy.setCopyType(CopyType.valueOf(request.getCopyType()));
        bookCopy.setStatus(CopyStatus.valueOf(request.getStatus() != null ? request.getStatus() : "AVAILABLE"));

        return mapToResponse(bookCopyRepository.save(bookCopy));
    }

    // Get all copies for a book
    public List<BookCopyResponse> getCopiesByBookId(UUID bookId) {
        return bookCopyRepository.findByBook_BookId(bookId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Update Book Copy
    public BookCopyResponse updateBookCopy(UUID copyId, BookCopyRequest request) {
        BookCopy bookCopy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new RuntimeException("Book copy not found"));

        bookCopy.setBarcode(request.getBarcode());
        bookCopy.setLocation(request.getLocation());
        bookCopy.setCopyType(CopyType.valueOf(request.getCopyType()));
        if (request.getStatus() != null) {
            bookCopy.setStatus(CopyStatus.valueOf(request.getStatus()));
        }

        return mapToResponse(bookCopyRepository.save(bookCopy));
    }

    // Delete Book Copy
    public void deleteBookCopy(UUID copyId) {
        BookCopy bookCopy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new RuntimeException("Book copy not found"));

        // Check if copy is currently borrowed
        if (bookCopy.getStatus() == CopyStatus.BORROWED) {
            throw new RuntimeException("Cannot delete a borrowed book copy");
        }

        bookCopyRepository.delete(bookCopy);
    }

    // Issue Book
//    public IssueHistoryResponse issueBook(IssueBookRequest request) {
//        BookCopy bookCopy = bookCopyRepository.findById(UUID.fromString(request.getCopyId()))
//                .orElseThrow(() -> new RuntimeException("Book copy not found"));
//
//        if (bookCopy.getStatus() != CopyStatus.AVAILABLE) {
//            throw new RuntimeException("Book copy is not available for issue");
//        }
//
//        // Update book copy status
//        bookCopy.setStatus(CopyStatus.BORROWED);
//        bookCopy.setCurrentBorrower(String.valueOf(request.getMemberId()));
//        bookCopy.setDueDate(null);
//        bookCopyRepository.save(bookCopy);
//
//        // Create issue history
//        IssueHistory issueHistory = new IssueHistory();
//        issueHistory.setBookCopy(bookCopy);
//        issueHistory.setBook(bookCopy.getBook());
//        issueHistory.setMemberName(request.getMemberName());
////        issueHistory.setMemberId(request.getMemberId());
//        issueHistory.setIssueDate(LocalDateTime.now());
//        issueHistory.setDueDate(request.getDueDate());
//        issueHistory.setNotes(request.getNotes());
//        issueHistory.setReturned(false);
//
//        return mapToHistoryResponse(issueHistoryRepository.save(issueHistory));
//    }

    // Return Book
    public IssueHistoryResponse returnBook(UUID copyId) {
        BookCopy bookCopy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new RuntimeException("Book copy not found"));

        if (bookCopy.getStatus() != CopyStatus.BORROWED) {
            throw new RuntimeException("Book copy is not currently borrowed");
        }

        // Find active issue
        List<IssueHistory> activeIssues = issueHistoryRepository
                .findByBookCopy_CopyIdAndReturnedFalse(copyId);

        if (activeIssues.isEmpty()) {
            throw new RuntimeException("No active issue found for this copy");
        }

        IssueHistory issueHistory = activeIssues.get(0);
        issueHistory.setReturnDate(LocalDateTime.now());
        issueHistory.setReturned(true);

        // Update book copy status
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopy.setCurrentBorrower(null);
        bookCopy.setDueDate(null);
        bookCopyRepository.save(bookCopy);

        return mapToHistoryResponse(issueHistoryRepository.save(issueHistory));
    }

    // Get issue history for a book
    public List<IssueHistoryResponse> getIssueHistory(UUID bookId) {
        return issueHistoryRepository.findByBookIdOrderByIssueDateDesc(bookId)
                .stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());
    }

    // Get copy statistics
    public CopyStatistics getCopyStatistics(UUID bookId) {
        long total = bookCopyRepository.findByBook_BookId(bookId).size();
        long available = bookCopyRepository.countByBook_BookIdAndStatus(bookId, CopyStatus.AVAILABLE);
        long borrowed = bookCopyRepository.countByBook_BookIdAndStatus(bookId, CopyStatus.BORROWED);

        CopyStatistics stats = new CopyStatistics();
        stats.setTotalCopies(total);
        stats.setAvailableCopies(available);
        stats.setBorrowedCopies(borrowed);

        return stats;
    }

    // Mapping methods
//    private BookCopyResponse mapToResponse(BookCopy bookCopy) {
//        BookCopyResponse response = new BookCopyResponse();
//        response.setCopyId(bookCopy.getCopyId().toString());
//        response.setBookId(bookCopy.getBook().getBookId().toString());
//        response.setBarcode(bookCopy.getBarcode());
//        response.setLocation(bookCopy.getLocation());
//        response.setCopyType(String.valueOf(bookCopy.getCopyType()));
//        response.setStatus(bookCopy.getStatus().name());
//        response.setCurrentBorrower(bookCopy.getCurrentBorrower());
//        response.setDueDate(bookCopy.getDueDate());
//        response.setCreatedAt(bookCopy.getCreatedAt());
//        return response;
//    }

    private BookCopyResponse mapToResponse(BookCopy bookCopy) {
        BookCopyResponse response = new BookCopyResponse();
        response.setCopyId(bookCopy.getCopyId().toString());
        response.setBookId(bookCopy.getBook().getBookId().toString());
        response.setBarcode(bookCopy.getBarcode());
        response.setLocation(bookCopy.getLocation());
        response.setCopyType(String.valueOf(bookCopy.getCopyType()));
        response.setStatus(bookCopy.getStatus().name());
        response.setCurrentBorrower(bookCopy.getCurrentBorrower());
        response.setDueDate(bookCopy.getDueDate());
        response.setCreatedAt(bookCopy.getCreatedAt());

        // Fetch member details if there's a current borrower
        if (bookCopy.getCurrentBorrower() != null) {
            try {
                UUID memberId = UUID.fromString(bookCopy.getCurrentBorrower());
                Member member = memberRepository.findById(memberId)
                        .orElse(null);

                if (member != null) {
                    MemberDTO memberDetails = new MemberDTO();
                    memberDetails.setMemberId(member.getMemberId());
                    memberDetails.setName(member.getFirstname() + " " + member.getLastname());
                    memberDetails.setEmail(member.getEmail());
                    memberDetails.setPhoneNumber(member.getPhone());
                    memberDetails.setMemberType(member.getMembershipType().toString());

                    response.setMemberDetails(memberDetails);
                }
            } catch (IllegalArgumentException e) {
            }
        }

        return response;
    }

    private IssueHistoryResponse mapToHistoryResponse(IssueHistory history) {
        IssueHistoryResponse response = new IssueHistoryResponse();
        response.setIssueId(history.getIssueId().toString());
        response.setMemberName(history.getMemberName());
//        response.setMemberId(history.getMemberId());
        response.setIssueDate(history.getIssueDate());
        response.setDueDate(history.getDueDate());
        response.setReturnDate(history.getReturnDate());
        response.setReturned(history.getReturned());
        response.setNotes(history.getNotes());
        return response;
    }

    @Data
    public static class CopyStatistics {
        private Long totalCopies;
        private Long availableCopies;
        private Long borrowedCopies;
    }
}