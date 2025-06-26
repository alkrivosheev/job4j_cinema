package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FileRepository;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FilmServiceImpl filmService;

    @Test
    void whenFindAllWithGenreThenReturnAllFilmsWithDetails() {
        Film film1 = new Film(1, "Film 1", "Description 1", 2020, 1, 16, 120, 1);
        Film film2 = new Film(2, "Film 2", "Description 2", 2021, 2, 12, 90, 2);
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(2, "Comedy");
        File file1 = new File(1, "file1.jpg", "path/to/file1.jpg");
        File file2 = new File(2, "file2.jpg", "path/to/file2.jpg");

        when(filmRepository.findAll()).thenReturn(List.of(film1, film2));
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre1));
        when(genreRepository.findById(2)).thenReturn(Optional.of(genre2));
        when(fileRepository.findById(1)).thenReturn(Optional.of(file1));
        when(fileRepository.findById(2)).thenReturn(Optional.of(file2));

        Collection<FilmDto> result = filmService.findAllWithGenre();

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(FilmDto::getName)).containsExactlyInAnyOrder("Film 1", "Film 2");
        assertThat(result.stream().map(FilmDto::getGenre)).containsExactlyInAnyOrder("Action", "Comedy");
        assertThat(result.stream().map(FilmDto::getFileName)).containsExactlyInAnyOrder("file1.jpg", "file2.jpg");
    }

    @Test
    void whenFindByGenreIdThenReturnFilteredFilms() {
        Film film1 = new Film(1, "Action Film", "Desc", 2020, 1, 16, 120, 1);
        Film film2 = new Film(2, "Comedy Film", "Desc", 2021, 2, 12, 90, 2);
        Genre genre1 = new Genre(1, "Action");
        File file1 = new File(1, "action.jpg", "path/action.jpg");

        when(filmRepository.findAll()).thenReturn(List.of(film1, film2));
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre1));
        when(fileRepository.findById(1)).thenReturn(Optional.of(file1));

        Collection<FilmDto> result = filmService.findByGenreId(1);

        assertThat(result).hasSize(1);
        FilmDto dto = result.iterator().next();
        assertThat(dto.getName()).isEqualTo("Action Film");
        assertThat(dto.getGenre()).isEqualTo("Action");
        assertThat(dto.getFileName()).isEqualTo("action.jpg");
    }

    @Test
    void whenFindAllGenresThenReturnAllGenres() {
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(2, "Comedy");

        when(genreRepository.findAll()).thenReturn(List.of(genre1, genre2));

        Collection<Genre> result = filmService.findAllGenres();

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(Genre::getName)).containsExactlyInAnyOrder("Action", "Comedy");
    }

    @Test
    void whenFindByIdExistsThenReturnFilm() {
        Film film = new Film(1, "Film", "Desc", 2023, 1, 16, 120, 1);
        when(filmRepository.findById(1)).thenReturn(Optional.of(film));

        Optional<Film> result = filmService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Film");
    }

    @Test
    void whenFindByIdNotExistsThenReturnEmpty() {
        when(filmRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Film> result = filmService.findById(999);

        assertThat(result).isEmpty();
    }

    @Test
    void whenConvertToDtoGenreNotFoundThenThrowException() {
        Film film = new Film(1, "Film", "Desc", 2023, 999, 16, 120, 1);
        File file = new File(1, "file.jpg", "path/file.jpg");
        when(filmRepository.findAll()).thenReturn(List.of(film));
        when(fileRepository.findById(1)).thenReturn(Optional.of(file));
        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.findAllWithGenre())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Genre not found");
    }
}