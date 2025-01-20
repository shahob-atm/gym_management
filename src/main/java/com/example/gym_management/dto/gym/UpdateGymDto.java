package com.example.gym_management.dto.gym;

import jakarta.validation.constraints.NotBlank;

public record UpdateGymDto(
        @NotBlank
        String name,
        @NotBlank
        String location,
        @NotBlank
        String gymId
) {
}
