package com.example.gym_management.repository;

import com.example.gym_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RoleRepo extends JpaRepository<Role, UUID> {
    List<Role> findAllByName(String name);

    @Query(value = "select * from role where name IN ('ROLE_SUPER_ADMIN')", nativeQuery = true)
    List<Role> findAdminRoles();

    @Query(value = "select id from role where name = :name ", nativeQuery = true)
    UUID findByName(String name);
}
