package com.example.gym_management.controller;

import com.example.gym_management.dto.admin.UserDto;
import com.example.gym_management.dto.rate.RateDto;
import com.example.gym_management.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> addUser(@RequestBody UserDto userDto) {
        return adminService.addUser(userDto);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> getUsers(@RequestHeader String Authorization, @RequestParam(required = false) String keyword) {
        return adminService.getUsers(Authorization, keyword);
    }

    @PostMapping("/rate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> addRate(@RequestBody RateDto rateDto) {
        return adminService.addRate(rateDto);
    }

    @GetMapping("/rate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> getRate(@RequestHeader String Authorization) {
        return adminService.getRate(Authorization);

    }

    @PostMapping("/user_rate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> addRateToUser(@RequestParam String rateId, @RequestParam String userId) {
        return adminService.addRateToUser(rateId, userId);
    }

    @PostMapping("/user_rate_day")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> addRateToUserDay(@RequestParam String user_rate_id) {
        return adminService.addRateToUserDay(user_rate_id);
    }

    @GetMapping("/user_rate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> getUserRate(@RequestHeader String Authorization) {
        return adminService.getUserRate(Authorization);
    }

    @GetMapping("/user_rate_day")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> getUserRateDay(@RequestParam String userRateId) {
        return adminService.getUserRateDay(userRateId);
    }

    @GetMapping("rate_history")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> getUserRateHistory(@RequestHeader String Authorization, @RequestParam String userId) {
        return adminService.getUserRateHistory(Authorization,userId);
    }

    @DeleteMapping("/user_rate_rate_history")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> deleteUserRateHistory(@RequestParam String userRateId) {
        return adminService.deleteUserRateHistory(userRateId);
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> getReport( @RequestHeader String Authorization) {

        return adminService.getReport(Authorization);
    }

    @DeleteMapping("/delete_rate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> deleteRate(@RequestParam String rateId) {
        return adminService.deleteRate(rateId);
    }

    @PostMapping("/start_user_rate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public HttpEntity<?> startUserRate(@RequestParam String userRateId) {
        return adminService.startUserRate(userRateId);
    }
}
