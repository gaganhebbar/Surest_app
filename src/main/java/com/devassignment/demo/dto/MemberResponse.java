package com.devassignment.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
}
