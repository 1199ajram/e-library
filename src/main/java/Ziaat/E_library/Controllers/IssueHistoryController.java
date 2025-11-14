package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.IssueBookRequest;
import Ziaat.E_library.Dto.IssueHistory.IssueHistoryDTO;
import Ziaat.E_library.Dto.IssueHistory.ReturnBookRequest;
import Ziaat.E_library.Services.IssueHistoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
@Tag(name = "issues Management", description = "APIs for managing book issues")
public class IssueHistoryController {
    private final IssueHistoryService issueHistoryService;

    // Get current issues for a member
    @GetMapping("/member/{memberId}/current")
    public ResponseEntity<List<IssueHistoryDTO>> getCurrentIssuesByMember(@PathVariable UUID memberId) {
        List<IssueHistoryDTO> issues = issueHistoryService.getCurrentIssuesByMember(memberId);
        return ResponseEntity.ok(issues);
    }

    // Get borrowing history for a member
    @GetMapping("/member/{memberId}/history")
    public ResponseEntity<Page<IssueHistoryDTO>> getBorrowingHistory(
            @PathVariable UUID memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "issueDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<IssueHistoryDTO> history = issueHistoryService.getBorrowingHistory(memberId, pageable);
        return ResponseEntity.ok(history);
    }

    // Get all issues with pagination
    @GetMapping
    public ResponseEntity<Page<IssueHistoryDTO>> getAllIssues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "issueDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<IssueHistoryDTO> issues = issueHistoryService.getAllIssues(pageable);
        return ResponseEntity.ok(issues);
    }

    // Issue a book
    @PostMapping
    public ResponseEntity<IssueHistoryDTO> issueBook(@RequestBody IssueBookRequest request) {
        IssueHistoryDTO issue = issueHistoryService.issueBook(
                request.getCopyId(),
                request.getBookId(),
                request.getMemberId(),
                request.getNotes(),
                request.getIssuedBy()
        );
        return ResponseEntity.ok(issue);
    }

    // Return a book
    @PostMapping("/{issueId}/return")
    public ResponseEntity<IssueHistoryDTO> returnBook(
            @PathVariable UUID issueId,
            @RequestBody(required = false) ReturnBookRequest request
    ) {
        String returnedBy = request != null ? request.getReturnedBy() : null;
        IssueHistoryDTO issue = issueHistoryService.returnBook(issueId, returnedBy);
        return ResponseEntity.ok(issue);
    }

    // Renew a book
    @PostMapping("/{issueId}/renew")
    public ResponseEntity<IssueHistoryDTO> renewBook(@PathVariable UUID issueId) {
        IssueHistoryDTO issue = issueHistoryService.renewBook(issueId);
        return ResponseEntity.ok(issue);
    }

    // Get all overdue issues
    @GetMapping("/overdue")
    public ResponseEntity<List<IssueHistoryDTO>> getOverdueIssues() {
        List<IssueHistoryDTO> overdueIssues = issueHistoryService.getOverdueIssues();
        return ResponseEntity.ok(overdueIssues);
    }
}
