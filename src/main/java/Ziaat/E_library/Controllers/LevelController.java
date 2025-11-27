package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.LevelRequestDto;
import Ziaat.E_library.Dto.LevelResponseDto;
import Ziaat.E_library.Model.Level;
import Ziaat.E_library.Services.LevelService;
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
@RequestMapping("/api/levels")
@RequiredArgsConstructor
@Tag(name = "Level Management", description = "APIs for managing level in the library system")
public class LevelController {


    @Autowired
    private LevelService levelService;


    @Operation(
            summary = "Create a new level",
            description = "Creates a new level in the library system"
    )
    @PostMapping
    public ResponseEntity<LevelResponseDto> create(
            @Parameter(description = "level details", required = true)
            @RequestBody LevelRequestDto request) {
        LevelResponseDto level = levelService.createLevel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(level);
    }


    @Operation(
            summary = "Update level",
            description = "Updates an existing level information"
    )
    @PutMapping("/{id}")
    public ResponseEntity<LevelResponseDto> update(
            @Parameter(description = "level ID", required = true, example = "1")
            @PathVariable UUID id,
            @Parameter(description = "Updated level details", required = true)
            @RequestBody LevelRequestDto request) {
        return ResponseEntity.ok(levelService.updateLevel(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        levelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/active/program/{programId}")
    public List<LevelResponseDto> getActiveLevelsByProgram(@PathVariable UUID programId) {
        return levelService.getActiveLevelsByProgram(programId);
    }

    @Operation(
            summary = "Get level by ID",
            description = "Retrieves a specific level by their ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<LevelResponseDto> get(
            @Parameter(description = "level ID", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(levelService.getLevel(id));
    }

    @Operation(
            summary = "Get all level with pagination and search",
            description = "Retrieves a paginated list of level with optional search functionality"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved paginated list of level",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PageResponse<Level>> getAllLevel(
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
        Page<Level> levelPage = levelService.getAllLevel(page, size, sortBy, sortDir, search);

        PageResponse<Level> response = new PageResponse<>();
        response.setContent(levelPage.getContent());
        response.setPageNumber(levelPage.getNumber());
        response.setPageSize(levelPage.getSize());
        response.setTotalElements(levelPage.getTotalElements());
        response.setTotalPages(levelPage.getTotalPages());
        response.setLast(levelPage.isLast());
        response.setFirst(levelPage.isFirst());
        response.setHasNext(levelPage.hasNext());
        response.setHasPrevious(levelPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Level>> getActive() {
        return ResponseEntity.ok(levelService.getActive());
    }
}
