package com.example.gym_management.dto.rate;

import jakarta.validation.constraints.NotBlank;

public record RateDto(
        @NotBlank
        String adminName,
        @NotBlank
        Integer day,
        @NotBlank
        Integer everyDay,
        @NotBlank
        String name,
        @NotBlank
        Integer price
) {
}
