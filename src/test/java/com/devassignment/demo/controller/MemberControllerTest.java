package com.devassignment.demo.controller;

import com.devassignment.demo.dto.MemberRequest;
import com.devassignment.demo.dto.MemberResponse;
import com.devassignment.demo.dto.PagedResponse;
import com.devassignment.demo.entity.Member;
import com.devassignment.demo.services.MemberService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * GET /api/members
     */
    @Test
    void testPaginatedMembers() {

        MemberResponse member = MemberResponse.builder()
                .id(UUID.randomUUID())
                .firstName("gagan")
                .lastName("hebbar")
                .email("gagan.hebbar@example.com")
                .dateOfBirth(LocalDate.of(1996, 7, 31))
                .build();

        PagedResponse<MemberResponse> mockResponse = new PagedResponse<>(
                List.of(member),
                0,
                10,
                1,
                1,
                true,
                true
        );

        when(memberService.getMembers(any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(mockResponse);

        // Call controller
        PagedResponse<MemberResponse> response =
                memberController.getMembers("gagan", "hebbar", 0, 10, "lastName,asc");

        // Assertions
        assertNotNull(response);
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(1, response.getTotalElements());
        assertEquals("gagan", response.getData().get(0).getFirstName());

        // Verify actual call
        verify(memberService).getMembers(
                eq("gagan"),
                eq("hebbar"),
                eq(-1),  // VERY IMPORTANT
                eq(10),
                eq("lastName,asc")
        );
    }




    /**
     * GET /api/members/{id}
     */
    @Test
    void testMemberById() {
        UUID id = UUID.randomUUID();
        MemberResponse member = MemberResponse.builder()
                .id(id)
                .firstName("gagan")
                .lastName("hebbar")
                .email("gagan.hebbar@example.com")
                .dateOfBirth(LocalDate.of(1996, 7, 31))
                .build();
        when(memberService.getMemberById(id)).thenReturn(member);

        ResponseEntity<MemberResponse> result = memberController.getMemberById(id);

        // Assert
        assertEquals("gagan", result.getBody().getFirstName());
        verify(memberService).getMemberById(id);
    }

    /**
     * POST /api/members
     */
    @Test
    void testCreateMember() {
        MemberRequest request = new MemberRequest(
                "gagan", "hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan.hebbar@example.com"
        );

        MemberResponse created = MemberResponse.builder()
                .id(UUID.randomUUID())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        when(memberService.createMember(request)).thenReturn(created);

        ResponseEntity<MemberResponse> response = memberController.createMember(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("gagan", response.getBody().getFirstName());
        verify(memberService).createMember(request);
    }

    /**
     * PUT /api/members/{id}
     */
    @Test
    void shouldUpdateMember() {
        UUID id = UUID.randomUUID();

        MemberRequest request = new MemberRequest(
                "gagan", "Hebbar K A",
                LocalDate.of(1996, 7, 31),
                "gagan.hebbar31@example.com"
        );

        MemberResponse updated = MemberResponse.builder()
                .id(id)
                .firstName("gagan")
                .lastName("Hebbar K A")
                .email("gagan.hebbar31@example.com")
                .dateOfBirth(LocalDate.of(1996, 7, 31))
                .build();
        when(memberService.updateMember(id, request)).thenReturn(updated);

        ResponseEntity<MemberResponse> response = memberController.updateMember(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hebbar K A", response.getBody().getLastName());
        verify(memberService).updateMember(id, request);
    }

    /**
     * DELETE /api/members/{id}
     */
    @Test
    void shouldDeleteMember() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Map<String, String>> response = memberController.deleteMember(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Member deleted successfully", response.getBody().get("message"));
        verify(memberService).deleteMember(id);
    }
}
