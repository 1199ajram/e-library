package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.BookRequest;
import Ziaat.E_library.Dto.BookResponse;
import Ziaat.E_library.Dto.MemberResponse;
import Ziaat.E_library.Dto.minioDto.GetContentDto;
import Ziaat.E_library.Model.Books;
import Ziaat.E_library.Services.BooksService;
import Ziaat.E_library.Utils.PageResponse;
import Ziaat.E_library.Utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books Management", description = "APIs for managing Bookss in the library system")

public class BooksController {

    @Autowired
    private BooksService BooksService;


    @Operation(
            summary = "Create a new Books",
            description = "Creates a new Books in the library system"
    )
    @PostMapping
    public ResponseEntity<BookResponse> createBooks(
            @Parameter(description = "Books details", required = true)
            @RequestBody BookRequest request) {
        BookResponse Books = BooksService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Books);
    }

    @Operation(
            summary = "Get all Bookss with pagination and search",
            description = "Retrieves a paginated list of Bookss with optional search functionality"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved paginated list of Bookss",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PageResponse<Books>> getAllBookss(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "firstname")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir,

            @Parameter(description = "Search term to filter Bookss by name", example = "John")
            @RequestParam(required = false) String search,

            @Parameter(description = "Search term to filter Bookss by name", example = "John")
            @RequestParam(required = false) UUID categoryId
    ) {
        Page<Books> BooksPage = BooksService.getAllBooks(page, size, sortBy, sortDir, search,categoryId);

        PageResponse<Books> response = new PageResponse<>();
        response.setContent(BooksPage.getContent());
        response.setPageNumber(BooksPage.getNumber());
        response.setPageSize(BooksPage.getSize());
        response.setTotalElements(BooksPage.getTotalElements());
        response.setTotalPages(BooksPage.getTotalPages());
        response.setLast(BooksPage.isLast());
        response.setFirst(BooksPage.isFirst());
        response.setHasNext(BooksPage.hasNext());
        response.setHasPrevious(BooksPage.hasPrevious());

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get all book")
    @GetMapping("/all")
    public ResponseEntity<List<BookResponse>> getAll(
    ) {
        List<BookResponse> books = BooksService.getAll();
        return ResponseEntity.ok(books);
    }

    @Operation(
            summary = "Get Books by ID",
            description = "Retrieves a specific Books by their ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Books> getBooksById(
            @Parameter(description = "Books ID", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(BooksService.getBookById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        BooksService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update Books",
            description = "Updates an existing Books's information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Books updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Books not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBooks(
            @Parameter(description = "Books ID", required = true, example = "1")
            @PathVariable UUID id,
            @Parameter(description = "Updated Books details", required = true)
            @RequestBody BookRequest request) {
        return ResponseEntity.ok(BooksService.updateBook(id, request));
    }

    @GetMapping("/books/{id}/{attachment}/attachment")
    public ResponseEntity<String> downloadAttachment(@PathVariable UUID id,@PathVariable String attachment) {
        String presignedUrl = BooksService.getBookAttachment(id,attachment);
        return ResponseEntity.ok(presignedUrl);
    }





    @PostMapping("/get-content")
    public ResponseEntity<Response> getObject(@RequestBody GetContentDto request) throws IOException {
        Response response = BooksService.getObject(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/remove-content/{bucket}/{objectname}")
    public ResponseEntity<Response> removeObject(
            @PathVariable String bucket,
            @PathVariable String objectname) throws IOException {
        Response response = BooksService.removeObject(bucket, objectname);
        return ResponseEntity.ok(response);
    }

}
