package com.example.gym_management.repository;

import com.example.gym_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoleRepo extends JpaRepository<Role, UUID> {
    List<Role> findAllByName(String name);
}
