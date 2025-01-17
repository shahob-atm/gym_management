package com.example.gym_management.repository;

import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.User;
import com.example.gym_management.projection.DailySubscriptionCount;
import com.example.gym_management.projection.GymUsersProjection;
import com.example.gym_management.projection.MonthsProfitOverview;
import com.example.gym_management.projection.UsersVisitedGymToday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface GymRepo extends JpaRepository<Gym, UUID> {
    Gym findAllByAdmin(User admin);

    @Query(value = "select gu.users_id,\n" +
            "       ur.name,           " +
            "       u.full_name,\n" +
            "       u.username, \n " +
            "       ur.start_time,\n" +
            "       ur.end_time,\n" +
            "       ur.day,\n " +
            "       ur.active, " +
            "       ur.start_date," +
            "       ur.start_day, " +
            "       ur.id as user_rate_id " +
            "from gym g\n" +
            "         left join gym_users gu on g.id = gu.gym_id\n" +
            "         left join user_rate ur on ur.user_id = gu.users_id\n" +
            "         left join users u on gu.users_id = u.id " +
            "  where  g.admin_id = :adminId " +
            " and  (:keyword IS NULL OR u.username ilike CONCAT('%', :keyword, '%'))  " +
            " group by u.full_name, gu.users_id, u.username, ur.start_time, ur.end_time, ur.day, ur.active, ur.id " +
            " order by u.full_name  ", nativeQuery = true)
    List<GymUsersProjection> getUsers(UUID adminId, String keyword);

    @Query(value = "select\n" +
            "    u.full_name,\n" +
            "    u.username,\n" +
            "    ur.start_time,\n" +
            "    ur.day,\n" +
            "    ur.active\n" +
            "from gym g\n" +
            "         left join gym_users gu on g.id = gu.gym_id\n" +
            "         left join user_rate ur on ur.user_id = gu.users_id\n" +
            "         left join users u on gu.users_id = u.id\n" +
            "         left join user_rate_day urd on ur.id = urd.user_rate_id\n" +
            "where\n" +
            "    g.admin_id = :adminId \n" +
            "  and date_trunc('month', ur.start_time) = date_trunc('month', now())\n" +
            "  and ur.active = true\n" +
            "  and ur.day > 0\n" +
            "group by\n" +
            " gu.users_id, u.full_name, u.username, ur.start_time, ur.active, ur.day ", nativeQuery = true)
    List<DailySubscriptionCount> getDailySubscriptionCount(UUID adminId);

    @Query(value = "select u.full_name,\n" +
            "       u.username,\n" +
            "       urd.local_date,\n" +
            "       count(*) as visits_today\n" +
            "from gym g\n" +
            "         left join gym_users gu on g.id = gu.gym_id\n" +
            "         left join user_rate ur on ur.user_id = gu.users_id\n" +
            "         left join users u on gu.users_id = u.id\n" +
            "         left join user_rate_day urd on ur.id = urd.user_rate_id\n" +
            "where g.admin_id = :adminId \n" +
            "  and urd.local_date = current_date\n" +
            "group by gu.users_id, u.full_name, u.username, urd.local_date;", nativeQuery = true)
    List<UsersVisitedGymToday> getUsersVisitedGymToday(UUID adminId);

    @Query(value = "select to_char(ur.start_time, 'YYYY-MM') as month,\n" +
            "       sum(ur.price)                     as total_revenue\n" +
            "from gym g\n" +
            "          join gym_users gu on g.id = gu.gym_id\n" +
            "          join user_rate ur on ur.user_id = gu.users_id\n" +
            "where g.admin_id = :adminId \n" +
            "group by to_char(ur.start_time, 'YYYY-MM')\n" +
            "order by month;",nativeQuery = true)
    List<MonthsProfitOverview> getMonthsProfitOverview(UUID adminId);
}
