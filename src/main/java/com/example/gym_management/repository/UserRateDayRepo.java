package com.example.gym_management.repository;

import com.example.gym_management.entity.UserRateDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserRateDayRepo extends JpaRepository<UserRateDay, UUID> {
    List<UserRateDay> findAllByUserRateId(UUID userRateId);

    @Transactional
    @Modifying
    void deleteAllByUserRateId(UUID userRateId);
}
