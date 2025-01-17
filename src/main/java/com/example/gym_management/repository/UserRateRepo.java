package com.example.gym_management.repository;

import com.example.gym_management.entity.UserRate;
import com.example.gym_management.projection.UserHistoryRateProjection;
import com.example.gym_management.projection.UserRateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRateRepo extends JpaRepository<UserRate, UUID> {
    @Query(value = "select u.id as userId, ur.id as rateId ,u.full_name,u.username,ur.start_time, ur.end_time, ur.day,ur.name\n" +
            "from gym g\n" +
            "          join gym_users gu on g.id = gu.gym_id\n" +
            "          join user_rate ur on gu.users_id = ur.user_id\n" +
            "          join users u on gu.users_id = u.id\n" +
            "where g.admin_id = :adminId ", nativeQuery = true)
    List<UserRateProjection> getAllUserRate(UUID adminId);

    @Query(value = "select u.id,u.full_name,ur.start_time, ur.end_time, ur.price,ur.name\n" +
            "from gym g\n" +
            "          join gym_users gu on g.id = gu.gym_id\n" +
            "         join user_rate ur on gu.users_id = ur.user_id\n" +
            "          join users u on gu.users_id = u.id\n" +
            "where g.admin_id = :adminId and u.id = :userId ", nativeQuery = true)
    List<UserHistoryRateProjection> getHistoryUserRate(UUID adminId, UUID userId);
}
