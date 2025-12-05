package com.devassignment.demo.repository;

import com.devassignment.demo.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> , JpaSpecificationExecutor<Member> {
    boolean existsByEmail(String email);
}
