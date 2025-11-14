package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.*;
import Ziaat.E_library.Dto.IssueHistory.IssueHistoryDTO;
import Ziaat.E_library.Services.BookCopyService;
import Ziaat.E_library.Services.IssueHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/book-copies")
@RequiredArgsConstructor
@Tag(name = "Book Copies Management", description = "APIs for managing physical book copies")
public class BookCopyController {

    @Autowired
    private BookCopyService bookCopyService;

    @Autowired
    private IssueHistoryService issueHistoryService;

    @Operation(summary = "Create a new book copy")
    @PostMapping
    public ResponseEntity<BookCopyResponse> createBookCopy(@RequestBody BookCopyRequest request) {
        BookCopyResponse response = bookCopyService.createBookCopy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all copies for a book")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookCopyResponse>> getCopiesByBookId(@PathVariable UUID bookId) {
        List<BookCopyResponse> copies = bookCopyService.getCopiesByBookId(bookId);
        return ResponseEntity.ok(copies);
    }

    @Operation(summary = "Update a book copy")
    @PutMapping("/{copyId}")
    public ResponseEntity<BookCopyResponse> updateBookCopy(
            @PathVariable UUID copyId,
            @RequestBody BookCopyRequest request) {
        BookCopyResponse response = bookCopyService.updateBookCopy(copyId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a book copy")
    @DeleteMapping("/{copyId}")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable UUID copyId) {
        bookCopyService.deleteBookCopy(copyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Issue a book to a member")
    @PostMapping("/issue")
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


    @Operation(summary = "Return a borrowed book")
    @PostMapping("/{copyId}/return")
    public ResponseEntity<IssueHistoryResponse> returnBook(@PathVariable UUID copyId) {
        IssueHistoryResponse response = bookCopyService.returnBook(copyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get issue history for a book")
    @GetMapping("/history/book/{bookId}")
    public ResponseEntity<List<IssueHistoryResponse>> getIssueHistory(@PathVariable UUID bookId) {
        List<IssueHistoryResponse> history = bookCopyService.getIssueHistory(bookId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Get copy statistics for a book")
    @GetMapping("/statistics/{bookId}")
    public ResponseEntity<BookCopyService.CopyStatistics> getCopyStatistics(@PathVariable UUID bookId) {
        BookCopyService.CopyStatistics stats = bookCopyService.getCopyStatistics(bookId);
        return ResponseEntity.ok(stats);
    }
}