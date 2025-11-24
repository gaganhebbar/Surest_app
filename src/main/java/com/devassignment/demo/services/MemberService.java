package com.devassignment.demo.services;

import com.devassignment.demo.entity.Member;
import com.devassignment.demo.dto.MemberRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface MemberService {
    Page<Member> getMembers(String firstName,
                                   String lastName,
                                   int page,
                                   int size,
                                   String sort);
    Member getMemberById(UUID id);

    Member createMember(@Valid MemberRequest request);

    Member updateMember(UUID id, @Valid MemberRequest request);

    void deleteMember(UUID id);
}
