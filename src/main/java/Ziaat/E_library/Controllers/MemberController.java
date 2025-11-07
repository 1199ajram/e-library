package Ziaat.E_library.Controllers;

import Ziaat.E_library.Dto.MemberRequest;
import Ziaat.E_library.Dto.MemberResponse;
import Ziaat.E_library.Services.MemberService;
import Ziaat.E_library.Utils.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members Management", description = "APIs for managing library members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Create a new member")
    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all members with pagination")
    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "memberId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {

        Page<MemberResponse> members = memberService.getAllMembers(page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Get member by ID")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @Operation(summary = "Update member")
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable UUID id,
            @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @Operation(summary = "Suspend member")
    @PostMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendMember(@PathVariable UUID id) {
        memberService.suspendMember(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate member")
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateMember(@PathVariable UUID id) {
        memberService.activateMember(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete member")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
