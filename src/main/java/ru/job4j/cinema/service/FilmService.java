package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import java.util.Collection;
import java.util.Optional;

public interface FilmService {

    Collection<FilmDto> findAllWithGenre();

    Collection<FilmDto> findByGenreId(int genreId);

    Optional<Film> findById(int id);

    Collection<Genre> findAllGenres();
}