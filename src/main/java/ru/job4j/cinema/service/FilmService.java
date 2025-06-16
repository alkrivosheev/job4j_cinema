package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Genre;
import java.util.Collection;

public interface FilmService {

    Collection<FilmDto> findAllWithGenre();

    Collection<FilmDto> findByGenreId(int genreId);

    Collection<Genre> findAllGenres();
}