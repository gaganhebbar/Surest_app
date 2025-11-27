package com.devassignment.demo.services;

import com.devassignment.demo.dto.MemberMapper;
import com.devassignment.demo.dto.MemberResponse;
import com.devassignment.demo.dto.PagedResponse;
import com.devassignment.demo.entity.Member;
import com.devassignment.demo.dto.MemberRequest;
import com.devassignment.demo.repository.MemberRepository;
import com.devassignment.demo.specification.MemberSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository repository;

    @Override
    public PagedResponse<MemberResponse> getMembers(String firstName,
                                                    String lastName,
                                                    int page,
                                                    int size,
                                                    String sort) {
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Specification<Member> spec = Specification
                .where(MemberSpecifications.firstNameContains(firstName))
                .and(MemberSpecifications.lastNameContains(lastName));
        Page<Member> memberPage = repository.findAll(spec, pageable);

        List<MemberResponse> dtoList = memberPage.getContent()
                .stream()
                .map(MemberMapper::toResponse)
                .toList();
        return new PagedResponse<>(
                dtoList,
                memberPage.getNumber(),
                memberPage.getSize(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                memberPage.isFirst(),
                memberPage.isLast()
        );
    }

    @Override
    @Cacheable(value = "members", key = "#id.toString()")
    public MemberResponse getMemberById(UUID id) {
        Member member = repository.findById(id).orElseThrow(() ->
                new RuntimeException("Member not found"));
        return MemberMapper.toResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Member member = new Member();
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setEmail(request.getEmail());

        Member newMember = repository.save(member);
        return MemberMapper.toResponse(newMember);
    }

    @Override
    @Transactional
    @CacheEvict(value = "members", key = "#id.toString()")
    public MemberResponse updateMember(UUID id, MemberRequest req) {

        Member existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        existing.setFirstName(req.getFirstName());
        existing.setLastName(req.getLastName());
        existing.setDateOfBirth(req.getDateOfBirth());
        existing.setEmail(req.getEmail());
        existing.setUpdatedAt(LocalDateTime.now());

        Member UpdatedMember = repository.save(existing);
        return MemberMapper.toResponse(UpdatedMember);
    }

    @Override
    @Transactional
    @CacheEvict(value = "members", key = "#id.toString()")
    public void deleteMember(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Member not found");
        }
        repository.deleteById(id);
    }
}
