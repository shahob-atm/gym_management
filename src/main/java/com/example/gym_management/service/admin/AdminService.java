package com.example.gym_management.service.admin;

import com.example.gym_management.dto.rate.RateDto;
import com.example.gym_management.dto.admin.UserDto;
import org.springframework.http.HttpEntity;

public interface AdminService {
    HttpEntity<?> addUser(UserDto userDto);

    HttpEntity<?> getUsers(String authorization, String keyword);

    HttpEntity<?> addRate(RateDto rateDto);

    HttpEntity<?> getRate(String authorization);

    HttpEntity<?> addRateToUser(String rateId, String userId);

    HttpEntity<?> addRateToUserDay(String userRateId);

    HttpEntity<?> getUserRate(String authorization);

    HttpEntity<?> getUserRateDay(String userRateId);

    HttpEntity<?> getUserRateHistory(String Authorization, String userId);

    HttpEntity<?> deleteUserRateHistory(String userRateId);

    HttpEntity<?> getReport(String authorization);

    HttpEntity<?> deleteRate(String rateId);

    HttpEntity<?> startUserRate(String userRateId);
}
