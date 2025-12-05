package com.devassignment.demo.repository;

import com.devassignment.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserName(String username);
}
