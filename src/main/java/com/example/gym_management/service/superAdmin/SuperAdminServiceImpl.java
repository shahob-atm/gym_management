package com.example.gym_management.service.superAdmin;

import com.example.gym_management.dto.gym.GymAdminDto;
import com.example.gym_management.dto.gym.GymDto;
import com.example.gym_management.dto.gym.UpdateGymAdminDto;
import com.example.gym_management.dto.gym.UpdateGymDto;
import com.example.gym_management.dto.superAdmin.SuperAdminDto;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.Role;
import com.example.gym_management.entity.User;
import com.example.gym_management.repository.GymRepo;
import com.example.gym_management.repository.RoleRepo;
import com.example.gym_management.repository.UserRepo;
import com.example.gym_management.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final GymRepo gymRepo;
    private final RoleRepo roleRepo;

    @Override
    public HttpEntity<?> update(SuperAdminDto supAdminDto, String authorization) {

        String id = jwtService.extractJwtToken(authorization);

        User user = userRepo.findById(UUID.fromString(id)).orElseThrow();
        user.setPassword(passwordEncoder.encode(supAdminDto.password()));
        user.setFullName(supAdminDto.fullName());
        user.setUsername(supAdminDto.username());
        User save = userRepo.save(user);
        String jwtToken = jwtService.generateJwtToken(save);
        HashMap<String, String> map = new HashMap<>();
        map.put("accessToken", jwtToken);
        map.put("login", user.getUsername());
        map.put("fullName", user.getFullName());
        map.put("role", "ROLE_SUPER_ADMIN");

        return ResponseEntity.ok(map);
    }

    @Override
    public HttpEntity<?> addGym(GymDto gymDto) {
        Gym gym = Gym.builder().location(gymDto.location()).name(gymDto.name()).build();
        gymRepo.save(gym);
        return ResponseEntity.ok(gym);
    }

    @Override
    public HttpEntity<?> addAdmin(GymAdminDto gymAdminDto) {
        List<Role> roleAdmin = roleRepo.findAllByName("ROLE_ADMIN");

        User admin = User.builder()
                .username(gymAdminDto.username())
                .password(passwordEncoder.encode(gymAdminDto.password()))
                .fullName(gymAdminDto.fullName())
                .roles(roleAdmin)
                .isEnabled(true).build();

        User saveAdmin = userRepo.save(admin);
        Gym gym = gymRepo.findById(UUID.fromString(gymAdminDto.GymId())).orElseThrow();
        gym.setAdmin(saveAdmin);
        Gym save = gymRepo.save(gym);
        return ResponseEntity.ok(save);
    }

    @Override
    public HttpEntity<?> getGym() {
        List<Gym> gyms = gymRepo.findAll();
        return ResponseEntity.ok(gyms);
    }

    @Override
    public void deleteGym(String gymId) {
        gymRepo.deleteById(UUID.fromString(gymId));
    }

    @Override
    public HttpEntity<?> updateGym(UpdateGymDto gymDto) {
        Gym gym = gymRepo.findById(UUID.fromString(gymDto.gymId())).orElseThrow();
        gym.setName(gymDto.name());
        gym.setLocation(gymDto.location());
        Gym save = gymRepo.save(gym);
        return ResponseEntity.ok(save);
    }

    @Override
    public HttpEntity<?> getGymAdmin(String gymId) {
        Gym gym = gymRepo.findById(UUID.fromString(gymId)).orElseThrow();
        return ResponseEntity.ok(gym.getAdmin());
    }

    @Override
    public HttpEntity<?> updateGymAdmin(UpdateGymAdminDto gymAdminDto) {
        Gym gym = gymRepo.findById(UUID.fromString(gymAdminDto.GymId())).orElseThrow();
        User user = userRepo.findById(gym.getAdmin().getId()).orElseThrow();
        user.setFullName(gymAdminDto.fullName());
        user.setUsername(gymAdminDto.username());
        user.setPassword(passwordEncoder.encode(gymAdminDto.password()));
        User save = userRepo.save(user);

        return ResponseEntity.ok(save);
    }
}
