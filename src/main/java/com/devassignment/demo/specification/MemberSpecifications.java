package com.devassignment.demo.specification;

import com.devassignment.demo.entity.Member;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecifications {
    public static Specification<Member> firstNameContains(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<Member> lastNameContains(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }
}
