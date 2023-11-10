package com.bytetype.amanises.repository;

import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByAddress(String address);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleType(RoleType roleName);

    Boolean existsByUsername(String username);

    Boolean existsByAddress(String address);
}