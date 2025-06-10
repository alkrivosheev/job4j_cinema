package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;

    @Override
    public Optional<Film> findById(int id) {
        return filmRepository.findById(id);
    }

    @Override
    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    @Override
    public Collection<FilmDto> findAllWithGenre() {
        return filmRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FilmDto> findByIdWithGenre(int id) {
        return filmRepository.findById(id)
                .map(this::convertToDto);
    }

    private FilmDto convertToDto(Film film) {
        var genre = genreRepository.findById(film.getGenreId())
                .orElseThrow(() -> new IllegalStateException("Genre not found"));
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .year(film.getYear())
                .genre(genre.getName())
                .minimalAge(film.getMinimalAge())
                .durationInMinutes(film.getDurationInMinutes())
                .build();
    }
}