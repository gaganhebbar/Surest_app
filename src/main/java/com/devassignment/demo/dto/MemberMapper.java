package com.devassignment.demo.dto;

import com.devassignment.demo.entity.Member;

public class MemberMapper {
    public static MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .email(member.getEmail())
                .dateOfBirth(member.getDateOfBirth())
                .build();
    }
}
