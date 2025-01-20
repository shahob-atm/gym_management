package com.example.gym_management.dto.gym;

import jakarta.validation.constraints.NotBlank;

public record GymDto(
        @NotBlank
        String name,
        @NotBlank
        String location
) {
}
