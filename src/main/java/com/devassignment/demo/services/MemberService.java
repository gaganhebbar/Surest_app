package com.devassignment.demo.services;

import com.devassignment.demo.dto.MemberResponse;
import com.devassignment.demo.dto.PagedResponse;
import com.devassignment.demo.entity.Member;
import com.devassignment.demo.dto.MemberRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface MemberService {
    PagedResponse<MemberResponse> getMembers(String firstName,
                                             String lastName,
                                             int page,
                                             int size,
                                             String sort);
    MemberResponse getMemberById(UUID id);

    MemberResponse createMember(@Valid MemberRequest request);

    MemberResponse updateMember(UUID id, @Valid MemberRequest request);

    void deleteMember(UUID id);
}
