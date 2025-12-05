package com.devassignment.demo.controller;

import com.devassignment.demo.dto.MemberResponse;
import com.devassignment.demo.entity.Member;
import com.devassignment.demo.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.TimeZone;
import java.util.UUID;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberControllerIntegrationTest {
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("integration_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private static UUID savedId;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Order(1)
    void createMember() throws Exception {
        String payload = """
            {
              "firstName": "Gagan",
              "lastName": "Hebbar",
              "dateOfBirth": "1996-07-31",
              "email": "integration@example.com"
            }
            """;

        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Gagan"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andDo(result -> {
                    MemberResponse r = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            MemberResponse.class
                    );
                    savedId = r.getId();
                });

        Assertions.assertEquals(1, repository.count());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @Order(2)
    void getMemberById() throws Exception {
        Member member = new Member();
        member.setLastName("B");
        member.setFirstName("A");
        member.setEmail("ab@example.com");
        member.setDateOfBirth(LocalDate.of(1990,1,1));
        member = repository.save(member);

        mockMvc.perform(get("/api/v1/members/" + member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("A"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @Order(3)
    void getMembers() throws Exception {
        for (int i = 0; i < 15; i++) {
            Member member = new Member();
            member.setFirstName("User" + i);
            member.setLastName("Test" + i);
            member.setEmail("u" + i + "@example.com");
            member.setDateOfBirth(LocalDate.of(1990,1,1));
            repository.save(member);
        }

        mockMvc.perform(get("/api/v1/members")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(15));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Order(4)
    void updateMember() throws Exception {

        Member member = new Member();
        member.setFirstName("Old");
        member.setLastName("Name");
        member.setEmail("old@example.com");
        member.setDateOfBirth(LocalDate.of(1990,1,1));
        member = repository.save(member);

        String updated = """
            {
              "firstName": "New",
              "lastName": "Updated",
              "dateOfBirth": "1990-01-01",
              "email": "old@example.com"
            }
            """;

        mockMvc.perform(put("/api/v1/members/" + member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Order(5)
    void deleteMember() throws Exception {
        Member member = new Member();
        member.setFirstName("X");
        member.setLastName("Y");
        member.setEmail("z@example.com");
        member.setDateOfBirth(LocalDate.of(1990,1,1));
        member = repository.save(member);

        mockMvc.perform(delete("/api/v1/members/" + member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Member deleted successfully"));

        Assertions.assertFalse(repository.existsById(member.getId()));
    }
}
