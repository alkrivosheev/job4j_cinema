package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;
import java.util.Collection;
import java.util.Optional;

public interface FilmSessionService {

    Optional<FilmSession> findById(int id);

    Collection<FilmSession> findAll();

    Collection<FilmSessionDto> findAllWithFilmAndHall();

    Optional<FilmSessionDto> findByIdWithFilmAndHall(int id);

    Collection<FilmSession> findByFilmId(int filmId);
}