package ru.job4j.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmSessionDto {
    private int id;
    private String filmName;
    private String hallName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String duration;
    private int price;
    private int filmId;
    private int hallId;
}