package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FileRepository;
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
    private final FileRepository fileRepository;

    @Override
    public Collection<FilmDto> findAllWithGenre() {
        return filmRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<FilmDto> findByGenreId(int genreId) {
        return filmRepository.findAll().stream()
                .filter(f -> f.getGenreId() == genreId)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Genre> findAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Optional<Film> findById(int id) {
        return filmRepository.findById(id);
    }

    private FilmDto convertToDto(Film film) {
        var file = fileRepository.findById(film.getFileId())
                .orElseThrow(() -> new IllegalStateException("File not found"));
        var genre = genreRepository.findById(film.getGenreId())
                .orElseThrow(() -> new IllegalStateException("Genre not found"));
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getYear(),
                film.getMinimalAge(),
                film.getDurationInMinutes(),
                genre.getName(),
                file.getName()
        );
    }
}