package com.example.gym_management.security;

import com.example.gym_management.repository.UserRepo;
import com.example.gym_management.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Filter extends OncePerRequestFilter {
    private final UserRepo userRepo;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        try {
            if (token != null) {
                String id = jwtService.extractJwtToken(token);
                UserDetails users = userRepo.findById(UUID.fromString(id)).orElseThrow();

                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(users.getUsername(), null, users.getAuthorities()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
