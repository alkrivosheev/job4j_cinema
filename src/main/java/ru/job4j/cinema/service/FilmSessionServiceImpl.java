package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmSessionServiceImpl implements FilmSessionService {
    private final FilmSessionRepository filmSessionRepository;
    private final FilmRepository filmRepository;
    private final HallRepository hallRepository;

    @Override
    public Optional<FilmSession> findById(int id) {
        return filmSessionRepository.findById(id);
    }

    @Override
    public Collection<FilmSession> findAll() {
        return filmSessionRepository.findAll();
    }

    @Override
    public Collection<FilmSessionDto> findAllWithFilmAndHall() {
        return filmSessionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FilmSessionDto> findByIdWithFilmAndHall(int id) {
        return filmSessionRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Collection<FilmSession> findByFilmId(int filmId) {
        return filmSessionRepository.findByFilmId(filmId);
    }

    private FilmSessionDto convertToDto(FilmSession session) {
        var film = filmRepository.findById(session.getFilmId())
                .orElseThrow(() -> new IllegalStateException("Film not found"));
        var hall = hallRepository.findById(session.getHallId())
                .orElseThrow(() -> new IllegalStateException("Hall not found"));
        Duration duration = Duration.between(session.getStartTime().toLocalDateTime(), session.getEndTime().toLocalDateTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        String filmDuration = hours + ":" + minutes;
        return FilmSessionDto.builder()
                .id(session.getId())
                .filmName(film.getName())
                .hallName(hall.getName())
                .startTime(session.getStartTime().toLocalDateTime())
                .endTime(session.getEndTime().toLocalDateTime())
                .duration(filmDuration)
                .price(session.getPrice())
                .filmId(film.getId())
                .hallId(hall.getId())
                .build();
    }
}