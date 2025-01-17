package com.example.gym_management.repository;

import com.example.gym_management.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RateRepo extends JpaRepository<Rate, UUID> {
    List<Rate> findByAdminId(UUID adminId);
}
