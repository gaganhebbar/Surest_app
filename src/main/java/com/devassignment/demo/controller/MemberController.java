package com.devassignment.demo.controller;

import com.devassignment.demo.dto.MemberResponse;
import com.devassignment.demo.entity.Member;
import com.devassignment.demo.dto.MemberRequest;
import com.devassignment.demo.dto.PagedResponse;
import com.devassignment.demo.services.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    @Autowired
    private MemberService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public PagedResponse<MemberResponse> getMembers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort
    ) {
        int adjustedPage = page - 1;
        return service.getMembers(firstName, lastName, adjustedPage, size, sort);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable UUID id) {
        MemberResponse member = service.getMemberById(id);
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody MemberRequest request
    ) {
        MemberResponse member = service.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable UUID id,
            @Valid @RequestBody MemberRequest request
    ) {
        MemberResponse updated = service.updateMember(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable UUID id) {
        service.deleteMember(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Member deleted successfully");
        return ResponseEntity.ok(response);
    }
}
