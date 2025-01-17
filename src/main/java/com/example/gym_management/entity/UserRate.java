package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @CreationTimestamp
    private LocalDate startTime;
    private LocalDate endTime;
    @ManyToOne
    private User user;
    private Boolean active = true;
    private Integer rateDay;
    private String name;
    private Integer day;
    private Integer price;
    private Boolean isStarted = false;
    private LocalDate startDate;
    private Integer startDay;
}

