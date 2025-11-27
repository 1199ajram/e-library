package Ziaat.E_library.Controllers;


import Ziaat.E_library.Dto.ProgramRequestDto;
import Ziaat.E_library.Dto.ProgramResponseDto;
import Ziaat.E_library.Model.Level;
import Ziaat.E_library.Model.Program;

import Ziaat.E_library.Services.ProgramsServices;
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
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@Tag(name = "Programs Management", description = "APIs for managing programs in the library system")
public class ProgramController {

    @Autowired
    private ProgramsServices programService;


    @Operation(
            summary = "Create a new programs",
            description = "Creates a new programs in the library system"
    )
    @PostMapping
    public ResponseEntity<ProgramResponseDto> create(
            @Parameter(description = "programs details", required = true)
            @RequestBody ProgramRequestDto request) {
        ProgramResponseDto program = programService.createProgram(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(program);
    }


    @Operation(
            summary = "Update programs",
            description = "Updates an existing programs information"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProgramResponseDto> update(
            @Parameter(description = "programs ID", required = true, example = "1")
            @PathVariable UUID id,
            @Parameter(description = "Updated programs details", required = true)
            @RequestBody ProgramRequestDto request) {
        return ResponseEntity.ok(programService.updateProgram(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        programService.deleteProgram(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get programs by ID",
            description = "Retrieves a specific programs by their ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProgramResponseDto> get(
            @Parameter(description = "programs ID", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(programService.getProgram(id));
    }

    @Operation(
            summary = "Get all programs with pagination and search",
            description = "Retrieves a paginated list of programs with optional search functionality"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved paginated list of programs",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PageResponse<Program>> getAllPrograms(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "name")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir,

            @Parameter(description = "Search term to filter authors by name", example = "Book")
            @RequestParam(required = false) String search
    ) {
        Page<Program> programsPage = programService.getAllPrograms(page, size, sortBy, sortDir, search);

        PageResponse<Program> response = new PageResponse<>();
        response.setContent(programsPage.getContent());
        response.setPageNumber(programsPage.getNumber());
        response.setPageSize(programsPage.getSize());
        response.setTotalElements(programsPage.getTotalElements());
        response.setTotalPages(programsPage.getTotalPages());
        response.setLast(programsPage.isLast());
        response.setFirst(programsPage.isFirst());
        response.setHasNext(programsPage.hasNext());
        response.setHasPrevious(programsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/active/category/{categorId}")
    public List<ProgramResponseDto> getActiveProgramByCategory(@PathVariable UUID categorId) {
        return programService.getActiveProgramByCategory(categorId);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Program>> getActive() {
        return ResponseEntity.ok(programService.getActive());
    }
}
