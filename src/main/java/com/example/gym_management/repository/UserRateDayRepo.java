package com.example.gym_management.repository;

import com.example.gym_management.entity.UserRateDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRateDayRepo extends JpaRepository<UserRateDay, UUID> {
}
