package com.example.gym_management.service.superAdmin;

import com.example.gym_management.dto.gym.GymAdminDto;
import com.example.gym_management.dto.gym.GymDto;
import com.example.gym_management.dto.gym.UpdateGymAdminDto;
import com.example.gym_management.dto.gym.UpdateGymDto;
import com.example.gym_management.dto.superAdmin.SuperAdminDto;
import org.springframework.http.HttpEntity;

public interface SuperAdminService {
    HttpEntity<?> update(SuperAdminDto superAdminDto, String authorization);

    HttpEntity<?> addGym(GymDto gymDto);

    HttpEntity<?> addAdmin(GymAdminDto gymAdminDto);

    HttpEntity<?> getGym();

    void deleteGym(String gymId);

    HttpEntity<?> updateGym(UpdateGymDto gymDto);

    HttpEntity<?> getGymAdmin(String gymId);

    HttpEntity<?> updateGymAdmin(UpdateGymAdminDto gymAdminDto);
}
