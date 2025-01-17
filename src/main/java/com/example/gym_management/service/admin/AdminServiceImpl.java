package com.example.gym_management.service.admin;

import com.example.gym_management.dto.admin.UserDto;
import com.example.gym_management.dto.rate.RateDto;
import com.example.gym_management.entity.*;
import com.example.gym_management.projection.*;
import com.example.gym_management.repository.*;
import com.example.gym_management.service.jwt.JwtService;
import com.example.gym_management.wrapper.GymReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepo userRepo;
    private final GymRepo gymRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtServiceImpl;
    private final RateRepo rateRepo;
    private final UserRateRepo userRateRepo;
    private final UserRateDayRepo userRateDayRepo;

    @Override
    public HttpEntity<?> addUser(UserDto userDto) {
        User admin = userRepo.findByUsername(userDto.adminName())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        List<Role> roleUser = roleRepo.findAllByName("ROLE_USER");

        User user = User.builder()
                .username(userDto.username())
                .password(passwordEncoder.encode(userDto.password()))
                .fullName(userDto.fullName())
                .roles(roleUser)
                .isEnabled(true)
                .build();

        User savedUser = userRepo.save(user);

        Gym gym = gymRepo.findAllByAdmin(admin);

        List<User> currentUsers = gym.getUsers();

        if (currentUsers == null) {
            currentUsers = new ArrayList<>();
        }

        currentUsers.add(savedUser);

        gym.setUsers(currentUsers);

        Gym savedGym = gymRepo.save(gym);

        return ResponseEntity.ok(savedGym);
    }

    @Override
    public HttpEntity<?> getUsers(String authorization, String keyword) {
        LocalDate today = LocalDate.now();
        String id = jwtServiceImpl.extractJwtToken(authorization);
        List<GymUsersProjection> gymUsersProjections = gymRepo.getUsers(UUID.fromString(id), keyword);
        for (GymUsersProjection gymUsersProjection : gymUsersProjections) {
            String endTime = gymUsersProjection.getEndTime();
            if (endTime != null && LocalDate.parse(endTime).isBefore(today)) {

                String userRateId = gymUsersProjection.getUserRateId();
                UserRate userRate = userRateRepo.findById(UUID.fromString(userRateId)).orElseThrow();
                userRate.setActive(false);
                userRateRepo.save(userRate);
            }
        }
        List<GymUsersProjection> gymRepoUsersUpdate = gymRepo.getUsers(UUID.fromString(id), keyword);

        Map<UUID, List<GymUsersProjection>> groupedByUserId = gymRepoUsersUpdate.stream()
                .collect(Collectors.groupingBy(user -> UUID.fromString(user.getUsersId())));

        List<GymUsersProjection> filteredList = new ArrayList<>();

        for (Map.Entry<UUID, List<GymUsersProjection>> entry : groupedByUserId.entrySet()) {
            List<GymUsersProjection> userEntries = entry.getValue();

            List<GymUsersProjection> validEntries = userEntries.stream()
                    .filter(user -> (user.getDay() != null && user.getDay() > 0) || Boolean.TRUE.equals(user.getActive()))
                    .toList();

            if (!validEntries.isEmpty()) {
                Comparator<GymUsersProjection> comparator = Comparator.comparing((GymUsersProjection user) ->
                                Boolean.TRUE.equals(user.getActive()))
                        .thenComparing(user -> user.getDay() != null ? user.getDay() : Integer.MAX_VALUE);

                GymUsersProjection bestEntry = validEntries.stream()
                        .max(comparator)
                        .orElse(validEntries.get(0));
                filteredList.add(bestEntry);
            } else {
                filteredList.add(userEntries.get(0));
            }
        }

        return ResponseEntity.ok(filteredList);
    }

    @Override
    public HttpEntity<?> addRate(RateDto rateDto) {
        User admin = userRepo.findByUsername(rateDto.adminName()).orElseThrow();
        Rate build = Rate.builder().admin(admin).day(rateDto.day()).everyDay(rateDto.everyDay()).name(rateDto.name()).price(rateDto.price()).build();
        Rate save = rateRepo.save(build);
        return ResponseEntity.ok(save);
    }

    @Override
    public HttpEntity<?> getRate(String authorization) {
        String id = jwtServiceImpl.extractJwtToken(authorization);
        List<Rate> byAdminId = rateRepo.findByAdminId(UUID.fromString(id));
        return ResponseEntity.ok(byAdminId);
    }

    @Override
    public HttpEntity<?> addRateToUser(String rateId, String userId) {
        Rate rate = rateRepo.findById(UUID.fromString(rateId)).orElseThrow();
        User user = userRepo.findById(UUID.fromString(userId)).orElseThrow();

        UserRate build = UserRate.builder().user(user).day(rate.getDay()).price(rate.getPrice()).active(true).name(rate.getName())
                .startDay(rate.getEveryDay()).startDate(LocalDate.now()).startTime(null).build();
        UserRate save = userRateRepo.save(build);
        return ResponseEntity.ok(save);
    }

    @Override
    public HttpEntity<?> addRateToUserDay(String userRateId) {
        UserRate userRate = userRateRepo.findById(UUID.fromString(userRateId)).orElseThrow();
        if (userRate.getDay() > 0) {
            userRate.setDay(userRate.getDay() - 1);
            UserRate updateRate = userRateRepo.save(userRate);
            UserRateDay build = UserRateDay.builder().userRate(updateRate).localDate(LocalDate.now()).localTime(LocalTime.now()).build();
            UserRateDay save = userRateDayRepo.save(build);
            return ResponseEntity.ok(save);
        }
        return ResponseEntity.ok("error day");
    }

    @Override
    public HttpEntity<?> getUserRate(String authorization) {
        String id = jwtServiceImpl.extractJwtToken(authorization);
        List<UserRateProjection> allUserRate = userRateRepo.getAllUserRate(UUID.fromString(id));
        return ResponseEntity.ok(allUserRate);
    }

    @Override
    public HttpEntity<?> getUserRateDay(String userRateId) {
        List<UserRateDay> allByUserRateId = userRateDayRepo.findAllByUserRateId(UUID.fromString(userRateId));
        return ResponseEntity.ok(allByUserRateId);
    }

    @Override
    public HttpEntity<?> getUserRateHistory(String Authorization, String userId) {
        String id = jwtServiceImpl.extractJwtToken(Authorization);
        List<UserHistoryRateProjection> historyUserRate = userRateRepo.getHistoryUserRate(UUID.fromString(id), UUID.fromString(userId));
        return ResponseEntity.ok(historyUserRate);
    }

    @Override
    public HttpEntity<?> deleteUserRateHistory(String userRateId) {
        userRateDayRepo.deleteAllByUserRateId(UUID.fromString(userRateId));
        userRateRepo.deleteById(UUID.fromString(userRateId));
        return ResponseEntity.ok("success");
    }

    @Override
    public HttpEntity<?> getReport(String authorization) {
        String id = jwtServiceImpl.extractJwtToken(authorization);
        List<DailySubscriptionCount> dailySubscriptionCount = gymRepo.getDailySubscriptionCount(UUID.fromString(id));
        List<UsersVisitedGymToday> usersVisitedGymToday = gymRepo.getUsersVisitedGymToday(UUID.fromString(id));
        List<MonthsProfitOverview> monthsProfitOverview = gymRepo.getMonthsProfitOverview(UUID.fromString(id));

        GymReportResponse response = new GymReportResponse(dailySubscriptionCount, usersVisitedGymToday, monthsProfitOverview);

        return ResponseEntity.ok(response);
    }

    @Override
    public HttpEntity<?> deleteRate(String rateId) {
        rateRepo.deleteById(UUID.fromString(rateId));
        return ResponseEntity.ok("success");
    }

    @Override
    public HttpEntity<?> startUserRate(String userRateId) {
        UserRate userRate = userRateRepo.findById(UUID.fromString(userRateId)).orElseThrow();
        userRate.setIsStarted(true);
        userRate.setStartTime(LocalDate.now());
        userRate.setEndTime(LocalDate.now().plusDays(userRate.getStartDay()));
        userRate.setStartDate(null);
        UserRate save = userRateRepo.save(userRate);
        return ResponseEntity.ok(save);
    }
}
