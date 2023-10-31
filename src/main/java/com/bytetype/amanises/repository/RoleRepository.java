package com.bytetype.amanises.repository;


import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}