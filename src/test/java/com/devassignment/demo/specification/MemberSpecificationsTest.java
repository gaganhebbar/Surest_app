package com.devassignment.demo.specification;

import com.devassignment.demo.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberSpecificationsTest {

    @Test
    void testFirstNameContainsNullInput() {
        Specification<Member> spec = MemberSpecifications.firstNameContains(null);

        // Mocks
        Root<Member> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNull(result, "When firstName is null, predicate must be null");
    }

    @Test
    void testFirstNameContainsEmptyInput() {
        Specification<Member> spec = MemberSpecifications.firstNameContains("");

        Root<Member> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNull(result, "When firstName is empty, predicate must be null");
    }

    @Test
    void testFirstNameContainsValidInput() {
        Specification<Member> spec = MemberSpecifications.firstNameContains("john");

        Root<Member> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        when(cb.lower(root.get("firstName"))).thenReturn(null);
        when(cb.like(null, "%john%")).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
    }

    @Test
    void testLastNameContainsNullInput() {
        Specification<Member> spec = MemberSpecifications.lastNameContains(null);

        Root<Member> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNull(result);
    }

    @Test
    void testLastNameContainsValidInput() {
        Specification<Member> spec = MemberSpecifications.lastNameContains("doe");

        Root<Member> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        when(cb.lower(root.get("lastName"))).thenReturn(null);
        when(cb.like(null, "%doe%")).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
    }
}
