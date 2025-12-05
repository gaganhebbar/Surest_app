package com.devassignment.demo.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class MemberRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    public MemberRequest() { }
    public MemberRequest(String firstName, String lastName, LocalDate dateOfBirth, String email) {
        this.firstName  = firstName;
        this.lastName  = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }


}
