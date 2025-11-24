package com.devassignment.demo.controller;

import com.devassignment.demo.dto.MemberRequest;
import com.devassignment.demo.dto.MembersResponse;
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
        Member m = new Member(
                UUID.randomUUID(),
                "gagan",
                "hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan.hebbar@example.com",
                null, null
        );

        List<Member> members = List.of(m);

        Page<Member> mockPage = new PageImpl<>(
                members,
                org.springframework.data.domain.PageRequest.of(0, 10),
                members.size()
        );

        when(memberService.getMembers(null, null, 0, 10, "lastName,asc"))
                .thenReturn(mockPage);

        MembersResponse<Member> response =
                memberController.getMembers(null, null, 1, 10, "lastName,asc");

        // Assert
        assertEquals(1, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(1, response.getTotalElements());
        assertEquals("gagan", response.getData().get(0).getFirstName());
        verify(memberService).getMembers(null, null, 0, 10, "lastName,asc");
    }

    /**
     * GET /api/members/{id}
     */
    @Test
    void testMemberById() {
        UUID id = UUID.randomUUID();

        Member member = new Member(
                id,
                "gagan",
                "hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan.hebbar@example.com",
                null, null
        );

        when(memberService.getMemberById(id)).thenReturn(member);

        Member result = memberController.getMemberById(id);

        // Assert
        assertEquals("gagan", result.getFirstName());
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

        Member created = new Member(
                UUID.randomUUID(),
                "gagan",
                "hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan.hebbar@example.com",
                null, null
        );

        when(memberService.createMember(request)).thenReturn(created);

        ResponseEntity<Member> response = memberController.createMember(request);

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

        Member updated = new Member(
                id, "gagan", "Hebbar K A",
                LocalDate.of(1996, 7, 31),
                "gagan.hebbar31@example.com",
                null, null
        );

        when(memberService.updateMember(id, request)).thenReturn(updated);

        ResponseEntity<Member> response = memberController.updateMember(id, request);

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
