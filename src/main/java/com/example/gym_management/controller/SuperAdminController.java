package com.example.gym_management.controller;

import com.example.gym_management.dto.gym.GymAdminDto;
import com.example.gym_management.dto.gym.GymDto;
import com.example.gym_management.dto.gym.UpdateGymAdminDto;
import com.example.gym_management.dto.gym.UpdateGymDto;
import com.example.gym_management.dto.superAdmin.SuperAdminDto;
import com.example.gym_management.service.superAdmin.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/super_admin")
@RequiredArgsConstructor
public class SuperAdminController {
    final SuperAdminService superAdminService;

    @PutMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public HttpEntity<?> update(@RequestBody SuperAdminDto superAdminDto, @RequestHeader String Authorization) {

        return superAdminService.update(superAdminDto, Authorization);
    }

    @PostMapping("/gym")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public HttpEntity<?> addGym(@RequestBody GymDto gymDto) {

        return superAdminService.addGym(gymDto);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public HttpEntity<?> addAdmin(@RequestBody GymAdminDto gymAdminDto) {
        return superAdminService.addAdmin(gymAdminDto);
    }

    @GetMapping("/gym")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public HttpEntity<?> getGym() {
        return superAdminService.getGym();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public HttpEntity<?> deleteGym(@RequestParam String gymId) {
        superAdminService.deleteGym(gymId);
        return ResponseEntity.ok("success");
    }

    @PutMapping("/gym")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public HttpEntity<?> updateGym(@RequestBody UpdateGymDto gymDto) {
        return superAdminService.updateGym(gymDto);
    }

    @PatchMapping("/gym_admin")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public HttpEntity<?> getGymAdmin(@RequestParam String gymId) {
        return superAdminService.getGymAdmin(gymId);
    }

    @PutMapping("/admin")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public HttpEntity<?> updateGymAdmin(@RequestBody UpdateGymAdminDto gymAdminDto) {
        return superAdminService.updateGymAdmin(gymAdminDto);
    }
}

