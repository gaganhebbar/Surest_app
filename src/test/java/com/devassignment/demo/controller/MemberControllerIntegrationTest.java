package com.devassignment.demo.controller;

import com.devassignment.demo.dto.MemberRequest;
import com.devassignment.demo.entity.Member;
import com.devassignment.demo.services.MemberService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;


import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    private Member member;
    private UUID memberId;

    @BeforeEach
    void setup() {
        memberId = UUID.randomUUID();
        member = new Member(
                memberId,
                "Gagan",
                "Hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan@example.com",
                null,
                null
        );
    }

    /**
     * GET /api/members/{id}
     * @throws Exception
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void memberByIdForUserRole() throws Exception {
        when(memberService.getMemberById(memberId)).thenReturn(member);

        mockMvc.perform(get("/api/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Gagan"))
                .andExpect(jsonPath("$.email").value("gagan@example.com"));
    }

    /**
     * GET /api/members/{id} – Unauthorized
     * @throws Exception
     */
    @Test
    void authorizeCheckWhenNoToken() throws Exception {
        mockMvc.perform(get("/api/members/" + memberId))
                .andExpect(status().isUnauthorized());
    }

    /**
     * POST /api/members – Only ADMIN allowed
     * @throws Exception
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createMemberAsAdmin() throws Exception {
        MemberRequest req = new MemberRequest(
                "Gagan", "Hebbar",
                LocalDate.of(1996, 7, 31),
                "gagan@example.com"
        );

        when(memberService.createMember(Mockito.any())).thenReturn(member);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Gagan",
                                    "lastName": "Hebbar",
                                    "dateOfBirth": "1990-01-01",
                                    "email": "gagan@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Gagan"));
    }

    /**
     * POST /api/members – USER Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void createMemberAsUser() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Gagan",
                                    "lastName": "Hebbar",
                                    "dateOfBirth": "1990-01-01",
                                    "email": "gagan@example.com"
                                }
                                """))
                .andExpect(jsonPath("$.error").value("Access Denied"));
    }

    /**
     * PUT /api/members/{id}
     * @throws Exception
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateMemberAsAdmin() throws Exception {

        when(memberService.updateMember(eq(memberId), Mockito.any())).thenReturn(member);

        mockMvc.perform(put("/api/members/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Gagan",
                                    "lastName": "Updated",
                                    "dateOfBirth": "1996-07-31",
                                    "email": "gagan@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Hebbar"));
    }

    /**
     * PUT /api/members/{id}
     * @throws Exception
     */
    @Test
    @WithMockUser(roles = {"User"})
    void updateMemberAsUser() throws Exception {

        when(memberService.updateMember(eq(memberId), Mockito.any())).thenReturn(member);

        mockMvc.perform(put("/api/members/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Gagan",
                                    "lastName": "Updated",
                                    "dateOfBirth": "1996-07-31",
                                    "email": "gagan@example.com"
                                }
                                """))
                .andExpect(jsonPath("$.error").value("Access Denied"));
    }

    /**
     * DELETE /api/members/{id}
     * @throws Exception
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteMemberAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Member deleted successfully"));

        verify(memberService).deleteMember(memberId);
    }

    /**
     * DELETE – USER Forbidden
     * @throws Exception
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void deleteMemberAsUser() throws Exception {
        mockMvc.perform(delete("/api/members/" + memberId))
                .andExpect(jsonPath("$.error").value("Access Denied"));
    }
}
