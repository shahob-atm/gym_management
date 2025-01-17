package com.example.gym_management.repository;

import com.example.gym_management.entity.UserRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRateRepo extends JpaRepository<UserRate, UUID> {
}
