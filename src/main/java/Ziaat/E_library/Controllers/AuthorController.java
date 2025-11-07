package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.AuthorRequest;
import Ziaat.E_library.Dto.AuthorResponse;
import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Publisher;
import Ziaat.E_library.Services.AuthorService;
import Ziaat.E_library.Utils.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Author Management", description = "APIs for managing authors in the library system")
public class AuthorController {

    @Autowired
    private AuthorService authorService;


    @Operation(
            summary = "Create a new author",
            description = "Creates a new author in the library system"
    )
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(
            @Parameter(description = "Author details", required = true)
            @RequestBody AuthorRequest request) {
        AuthorResponse author = authorService.createAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }

    @Operation(
            summary = "Get all authors with pagination and search",
            description = "Retrieves a paginated list of authors with optional search functionality"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved paginated list of authors",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PageResponse<Author>> getAllAuthors(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "firstname")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir,

            @Parameter(description = "Search term to filter authors by name", example = "John")
            @RequestParam(required = false) String search
    ) {
        Page<Author> authorPage = authorService.getAllAuthors(page, size, sortBy, sortDir, search);

        PageResponse<Author> response = new PageResponse<>();
        response.setContent(authorPage.getContent());
        response.setPageNumber(authorPage.getNumber());
        response.setPageSize(authorPage.getSize());
        response.setTotalElements(authorPage.getTotalElements());
        response.setTotalPages(authorPage.getTotalPages());
        response.setLast(authorPage.isLast());
        response.setFirst(authorPage.isFirst());
        response.setHasNext(authorPage.hasNext());
        response.setHasPrevious(authorPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get author by ID",
            description = "Retrieves a specific author by their ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(
            @Parameter(description = "Author ID", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update author",
            description = "Updates an existing author's information"
    )
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @Parameter(description = "Author ID", required = true, example = "1")
            @PathVariable UUID id,
            @Parameter(description = "Updated author details", required = true)
            @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.updateAuthor(id, request));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Author>> getActive() {
        return ResponseEntity.ok(authorService.getActiveAuthors());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<AuthorResponse> get(@PathVariable Long id) {
//        return ResponseEntity.ok(authorService.getAuthor(id));
//    }
}

