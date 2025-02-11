package com.example.gym_management.util;

import com.example.gym_management.entity.Role;
import com.example.gym_management.entity.User;
import com.example.gym_management.repository.RoleRepo;
import com.example.gym_management.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MyCommandLineRunner implements CommandLineRunner {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;

    @Override
    public void run(String... args) throws Exception {
        List<Role> all = roleRepo.findAll();

        if (all.isEmpty()) {
            roleRepo.saveAll(List.of(
                    new Role("ROLE_ADMIN"),
                    new Role("ROLE_SUPER_ADMIN"),
                    new Role("ROLE_USER")
            ));

            List<Role> adminRoles = roleRepo.findAdminRoles();
            User user = User.builder().username("superadmin").fullName("superadmin")
                    .password(passwordEncoder.encode("123")).isEnabled(true)
                    .roles(adminRoles).build();
            userRepo.save(user);

            User adminUser = User.builder().username("admin").fullName("admin")
                    .password(passwordEncoder.encode("123")).isEnabled(true)
                    .roles(adminRoles).build();

            userRepo.save(adminUser);
        }
    }
}
