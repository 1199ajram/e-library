package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.FineRequest;
import Ziaat.E_library.Dto.fine.FineDTO;
import Ziaat.E_library.Dto.fine.FineSummaryDTO;
import Ziaat.E_library.Model.Fine;
import Ziaat.E_library.Services.FineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
@Tag(name = "Fines Management", description = "APIs for managing library fines")
public class FineController {

    private final FineService fineService;

    @Operation(summary = "Create a fine")
    @PostMapping
    public ResponseEntity<Fine> createFine(@RequestBody FineRequest request) {
        return ResponseEntity.ok(fineService.createFine(request));
    }

    @Operation(summary = "Calculate overdue fines")
    @PostMapping("/calculate-overdue")
    public ResponseEntity<Void> calculateOverdueFines() {
        fineService.calculateOverdueFines();
        return ResponseEntity.ok().build();
    }

//    @Operation(summary = "Get fines by member")
//    @GetMapping("/member/{memberId}")
//    public ResponseEntity<List<Fine>> getFinesByMember(@PathVariable UUID memberId) {
//        return ResponseEntity.ok(fineService.getFinesByMember(memberId));
//    }

    @Operation(summary = "Pay a fine")
    @PostMapping("/{id}/pay")
    public ResponseEntity<Fine> payFine(
            @PathVariable UUID id,
            @RequestParam String paymentMethod) {
        return ResponseEntity.ok(fineService.payFine(id, paymentMethod));
    }

    @Operation(summary = "Waive a fine")
    @PostMapping("/{id}/waive")
    public ResponseEntity<Fine> waiveFine(
            @PathVariable UUID id,
            @RequestParam String notes) {
        return ResponseEntity.ok(fineService.waiveFine(id, notes));
    }






    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<FineDTO>> getMemberFines(@PathVariable UUID memberId) {
        List<FineDTO> fines = fineService.getFinesByMemberId(memberId);
        return ResponseEntity.ok(fines);
    }

    @GetMapping("/member/{memberId}/summary")
    public ResponseEntity<FineSummaryDTO> getMemberFineSummary(@PathVariable UUID memberId) {
        FineSummaryDTO summary = fineService.getFineSummary(memberId);
        return ResponseEntity.ok(summary);
    }



}