package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    /**
     * Тест проверяет поиск существующего жанра по ID.
     * Ожидается:
     * - Возвращается Optional с ожидаемым жанром
     * - Проверяется вызов метода findById репозитория
     */
    @Test
    void whenFindExistingGenreByIdThenReturnGenre() {
        Genre expectedGenre = new Genre(1, "Action");
        when(genreRepository.findById(1)).thenReturn(Optional.of(expectedGenre));

        Optional<Genre> actualGenre = genreService.findById(1);

        assertThat(actualGenre)
                .isPresent()
                .contains(expectedGenre);
        verify(genreRepository).findById(1);
    }

    /**
     * Тест проверяет поиск несуществующего жанра по ID.
     * Ожидается:
     * - Возвращается пустой Optional
     * - Проверяется вызов метода findById репозитория
     */
    @Test
    void whenFindNonExistingGenreByIdThenReturnEmpty() {
        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Genre> actualGenre = genreService.findById(999);

        assertThat(actualGenre).isEmpty();
        verify(genreRepository).findById(999);
    }

    /**
     * Тест проверяет получение всех жанров.
     * Ожидается:
     * - Возвращается коллекция всех жанров
     * - Количество и содержимое жанров соответствует ожидаемому
     * - Проверяется вызов метода findAll репозитория
     */
    @Test
    void whenFindAllGenresThenReturnAllGenres() {
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(2, "Comedy");
        Genre genre3 = new Genre(3, "Drama");
        List<Genre> expectedGenres = List.of(genre1, genre2, genre3);

        when(genreRepository.findAll()).thenReturn(expectedGenres);

        Collection<Genre> actualGenres = genreService.findAll();

        assertThat(actualGenres)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedGenres);
        verify(genreRepository).findAll();
    }

    /**
     * Тест проверяет получение жанров при их отсутствии.
     * Ожидается:
     * - Возвращается пустая коллекция
     * - Проверяется вызов метода findAll репозитория
     */
    @Test
    void whenNoGenresExistThenReturnEmptyCollection() {
        when(genreRepository.findAll()).thenReturn(List.of());

        Collection<Genre> actualGenres = genreService.findAll();

        assertThat(actualGenres).isEmpty();
        verify(genreRepository).findAll();
    }
}