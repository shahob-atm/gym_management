package com.example.gym_management.repository;

import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GymRepo extends JpaRepository<Gym, UUID> {
    Gym findAllByAdmin(User admin);
}
