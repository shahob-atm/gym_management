package com.example.gym_management.wrapper;

import com.example.gym_management.projection.DailySubscriptionCount;
import com.example.gym_management.projection.MonthsProfitOverview;
import com.example.gym_management.projection.UsersVisitedGymToday;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class GymReportResponse {
    private List<DailySubscriptionCount> dailySubscriptionCount;
    private List<UsersVisitedGymToday> usersVisitedGymToday;
    private List<MonthsProfitOverview> monthsProfitOverview;

    public GymReportResponse(List<DailySubscriptionCount> dailySubscriptionCount,
                             List<UsersVisitedGymToday> usersVisitedGymToday,
                             List<MonthsProfitOverview> monthsProfitOverview) {
        this.dailySubscriptionCount = dailySubscriptionCount;
        this.usersVisitedGymToday = usersVisitedGymToday;
        this.monthsProfitOverview = monthsProfitOverview;
    }
}
