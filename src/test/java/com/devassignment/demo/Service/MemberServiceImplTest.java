package com.devassignment.demo.Service;

import com.devassignment.demo.dto.MemberRequest;
import com.devassignment.demo.entity.Member;
import com.devassignment.demo.repository.MemberRepository;
import com.devassignment.demo.services.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository repository;

    @InjectMocks
    private MemberServiceImpl service;

    private Member member;
    private UUID memberId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        memberId = UUID.randomUUID();
        member = new Member(
                memberId,
                "Gagan",
                "Hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan@example.com",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // -----------------------------------------------------------
    // GET MEMBERS (Pagination + Specs)
    // -----------------------------------------------------------
    @Test
    void getMembersPagedResults() {
        Page<Member> page = new PageImpl<>(List.of(member));

        when(repository.findAll(
                ArgumentMatchers.<Specification<Member>>any(),
                any(Pageable.class)
        )).thenReturn(page);

        Page<Member> result = service.getMembers("Gagan", "Hebbar", 0, 10, "firstName,asc");

        assertEquals(1, result.getContent().size());

        verify(repository).findAll(
                ArgumentMatchers.<Specification<Member>>any(),
                any(Pageable.class)
        );
    }

    /**
     * TEST GET MEMBER BY ID
     */
    @Test
    void getMemberById() {
        when(repository.findById(memberId)).thenReturn(Optional.of(member));

        Member result = service.getMemberById(memberId);

        assertEquals("Gagan", result.getFirstName());
        verify(repository).findById(memberId);
    }

    @Test
    void getMemberByIdNotFound() {
        when(repository.findById(memberId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getMemberById(memberId));

        assertEquals("Member not found", ex.getMessage());
    }

    /**
     * TEST CREATE MEMBER
     */
    @Test
    void createMember() {

        MemberRequest req = new MemberRequest(
                "Gagan",
                "Hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan@example.com"
        );

        when(repository.existsByEmail(req.getEmail())).thenReturn(false);
        when(repository.save(any(Member.class))).thenReturn(member);

        Member result = service.createMember(req);

        assertEquals("Gagan", result.getFirstName());
        verify(repository).save(any(Member.class));
    }

    @Test
    void createMemberWithDuplicateEmail() {
        MemberRequest req = new MemberRequest(
                "Gagan", "Hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan@example.com"
        );

        when(repository.existsByEmail(req.getEmail())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createMember(req));

        assertEquals("Email already exists", ex.getMessage());
    }


    /**
     * TEST UPDATE MEMBER
     */
    @Test
    void updateMember() {

        MemberRequest req = new MemberRequest(
                "Gagan",
                "Updated",
                LocalDate.of(1996, 7, 31),
                "gagan@example.com"
        );

        when(repository.findById(memberId)).thenReturn(Optional.of(member));
        when(repository.save(any(Member.class))).thenReturn(member);

        Member result = service.updateMember(memberId, req);

        assertEquals("Updated", result.getLastName());
        verify(repository).save(member);
    }

    @Test
    void updateMemberNotFound() {

        when(repository.findById(memberId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateMember(memberId, new MemberRequest()));

        assertEquals("Member not found", ex.getMessage());
    }

    /**
     * TEST DELETE MEMBER
     */
    @Test
    void deleteMember() {
        when(repository.existsById(memberId)).thenReturn(true);

        service.deleteMember(memberId);

        verify(repository).deleteById(memberId);
    }

    @Test
    void deleteMemberNotFound() {
        when(repository.existsById(memberId)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.deleteMember(memberId));

        assertEquals("Member not found", ex.getMessage());
        verify(repository, never()).deleteById(any());
    }
}
