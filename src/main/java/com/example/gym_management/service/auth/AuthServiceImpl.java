package com.example.gym_management.service.auth;

import com.example.gym_management.dto.user.LoginUserDto;
import com.example.gym_management.dto.user.UserDto;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.Role;
import com.example.gym_management.entity.User;
import com.example.gym_management.repository.GymRepo;
import com.example.gym_management.repository.RoleRepo;
import com.example.gym_management.repository.UserRepo;
import com.example.gym_management.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final GymRepo gymRepo;

    @Override
    public ResponseEntity<?> registerUser(UserDto userDto) {
        List<Role> roleUser = roleRepo.findAllByName("ROLE_USER");

        User user = User.builder().username(userDto.username()).fullName(userDto.fullName())
                .password(passwordEncoder.encode(userDto.password()))
                .roles(roleUser)
                .isEnabled(true).build();
        userRepo.save(user);

        return ResponseEntity.ok("registered");
    }

    @Override
    public ResponseEntity<?> loginUser(LoginUserDto loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.username(), loginUserDto.password())
        );

        User users = userRepo.findByUsername(loginUserDto.username()).orElseThrow();
        String jwtToken = jwtService.generateJwtToken(users);

        String rollName = users.getRoles() != null && !users.getRoles().isEmpty()
                ? users.getRoles().get(0).getName()
                : "UNKNOWN_ROLE";

        HashMap<String, String> map = new HashMap<>();
        map.put("accessToken", jwtToken);
        map.put("login", users.getUsername());
        map.put("fullName", users.getFullName());
        map.put("role", rollName);
        if (rollName.equals("ROLE_ADMIN")) {
            Gym gym = gymRepo.findAllByAdmin(users);
            map.put("gymName", gym.getName());
        }

        return ResponseEntity.ok(map);
    }
}
