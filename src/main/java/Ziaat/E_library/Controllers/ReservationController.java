package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.ReservationRequest;
import Ziaat.E_library.Model.Author;
import Ziaat.E_library.Model.Reservation;
import Ziaat.E_library.Services.ReservationService;
import Ziaat.E_library.Utils.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations Management", description = "APIs for managing book reservations")
public class ReservationController {

    private final ReservationService reservationService;


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
    public ResponseEntity<PageResponse<Reservation>> getAllReservation(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "reservationId")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir,

            @Parameter(description = "Search term to filter authors by name", example = "John")
            @RequestParam(required = false) String search,

            @Parameter(description = "Search ", example = "")
            @RequestParam(required = true) Reservation.ReservationStatus status

    ) {
        Page<Reservation> reservationPage = reservationService.getAllReservationByStatus(page, size, sortBy, sortDir, search,status);

        PageResponse<Reservation> response = new PageResponse<>();
        response.setContent(reservationPage.getContent());
        response.setPageNumber(reservationPage.getNumber());
        response.setPageSize(reservationPage.getSize());
        response.setTotalElements(reservationPage.getTotalElements());
        response.setTotalPages(reservationPage.getTotalPages());
        response.setLast(reservationPage.isLast());
        response.setFirst(reservationPage.isFirst());
        response.setHasNext(reservationPage.hasNext());
        response.setHasPrevious(reservationPage.hasPrevious());

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Create a new reservation")
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @Operation(summary = "Get reservations by member")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Reservation>> getReservationsByMember(@PathVariable UUID memberId) {
        return ResponseEntity.ok(reservationService.getReservationsByMember(memberId));
    }

    @Operation(summary = "Get reservations by book")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Reservation>> getReservationsByBook(@PathVariable UUID bookId) {
        return ResponseEntity.ok(reservationService.getReservationsByBook(bookId));
    }

    @Operation(summary = "Cancel a reservation")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable UUID id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Fulfill a reservation")
    @PostMapping("/{id}/fulfill")
    public ResponseEntity<Void> fulfillReservation(@PathVariable UUID id) {
        reservationService.fulfillReservation(id);
        return ResponseEntity.ok().build();
    }
}
