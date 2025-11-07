package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.AuthorRequest;
import Ziaat.E_library.Dto.AuthorResponse;
import Ziaat.E_library.Dto.PublisherRequest;
import Ziaat.E_library.Dto.PublisherResponse;
import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Publisher;
import Ziaat.E_library.Services.PublisherService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@Tag(name = "Publisher Management", description = "APIs for managing authors in the library system")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;


    @Operation(
            summary = "Create a new Publisher",
            description = "Creates a new Publisher in the library system"
    )
    @PostMapping
    public ResponseEntity<PublisherResponse> create(
            @Parameter(description = "Publisher details", required = true)
            @RequestBody PublisherRequest request) {
        PublisherResponse author = publisherService.createPublisher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }


    @Operation(
            summary = "Get all Publisher with pagination and search",
            description = "Retrieves a paginated list of Publisher with optional search functionality"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved paginated list of Publisher",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PageResponse<Publisher>> getAll(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "publisherId")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir,

            @Parameter(description = "Search term to filter authors by name", example = "John")
            @RequestParam(required = false) String search
    ) {
        Page<Publisher> publisherPage = publisherService.getAllPublisher(page, size, sortBy, sortDir, search);

        PageResponse<Publisher> response = new PageResponse<>();
        response.setContent(publisherPage.getContent());
        response.setPageNumber(publisherPage.getNumber());
        response.setPageSize(publisherPage.getSize());
        response.setTotalElements(publisherPage.getTotalElements());
        response.setTotalPages(publisherPage.getTotalPages());
        response.setLast(publisherPage.isLast());
        response.setFirst(publisherPage.isFirst());
        response.setHasNext(publisherPage.hasNext());
        response.setHasPrevious(publisherPage.hasPrevious());

        return ResponseEntity.ok(response);
    }





    @Operation(
            summary = "Update author",
            description = "Updates an existing publisher's information"
    )
    @PutMapping("/{id}")
    public ResponseEntity<PublisherResponse> update(
            @Parameter(description = "Author ID", required = true, example = "1")
            @PathVariable UUID id,
            @Parameter(description = "Updated publisher details", required = true)
            @RequestBody PublisherRequest request) {
        return ResponseEntity.ok(publisherService.updatePublisher(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Get author by ID",
            description = "Retrieves a specific author by their ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponse> getPublisherById(
            @Parameter(description = "Author ID", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(publisherService.getPublisher(id));
    }

    @Operation(
            summary = "Get Active Publisher",
            description = "Retrieves a specific Publisher "
    )
    @GetMapping("/active")
    public ResponseEntity<List<Publisher>> getPublisherActive() {
        return ResponseEntity.ok(publisherService.getActivePublisher());
    }
}

