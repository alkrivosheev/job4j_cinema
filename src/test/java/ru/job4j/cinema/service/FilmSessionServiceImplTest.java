package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmSessionServiceImplTest {

    @Mock
    private FilmSessionRepository filmSessionRepository;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private HallRepository hallRepository;

    @InjectMocks
    private FilmSessionServiceImpl filmSessionService;

    private Timestamp toTimestamp(LocalDateTime ldt) {
        return Timestamp.valueOf(ldt);
    }

    /**
     * Тест проверяет поиск сеанса по ID.
     * Ожидается:
     * - Возвращается Optional с ожидаемым сеансом
     * - Проверяется вызов метода findById репозитория
     */
    @Test
    void whenFindByIdThenReturnFilmSession() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        FilmSession expected = new FilmSession(1, 1, 1,
                toTimestamp(start), toTimestamp(end), 500);
        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(expected));

        Optional<FilmSession> actual = filmSessionService.findById(1);

        assertThat(actual).contains(expected);
        verify(filmSessionRepository).findById(1);
    }

    /**
     * Тест проверяет получение всех сеансов.
     * Ожидается:
     * - Возвращается коллекция всех сеансов
     * - Количество и содержимое сеансов соответствует ожидаемому
     * - Проверяется вызов метода findAll репозитория
     */
    @Test
    void whenFindAllThenReturnAllFilmSessions() {
        LocalDateTime now = LocalDateTime.now();
        FilmSession session1 = new FilmSession(1, 1, 1,
                toTimestamp(now), toTimestamp(now.plusHours(2)), 500);
        FilmSession session2 = new FilmSession(2, 2, 2,
                toTimestamp(now), toTimestamp(now.plusHours(3)), 600);
        when(filmSessionRepository.findAll()).thenReturn(List.of(session1, session2));

        Collection<FilmSession> actual = filmSessionService.findAll();

        assertThat(actual).hasSize(2).containsExactly(session1, session2);
        verify(filmSessionRepository).findAll();
    }

    /**
     * Тест проверяет поиск сеансов по ID фильма.
     * Ожидается:
     * - Возвращаются только сеансы для указанного фильма
     * - Проверяется вызов метода findByFilmId репозитория
     */
    @Test
    void whenFindByFilmIdThenReturnSessionsForFilm() {
        LocalDateTime now = LocalDateTime.now();
        FilmSession session1 = new FilmSession(1, 1, 1,
                toTimestamp(now), toTimestamp(now.plusHours(2)), 500);
        FilmSession session2 = new FilmSession(2, 1, 2,
                toTimestamp(now), toTimestamp(now.plusHours(3)), 600);
        when(filmSessionRepository.findByFilmId(1)).thenReturn(List.of(session1, session2));

        Collection<FilmSession> actual = filmSessionService.findByFilmId(1);

        assertThat(actual).hasSize(2).allMatch(s -> s.getFilmId() == 1);
        verify(filmSessionRepository).findByFilmId(1);
    }

    /**
     * Тест проверяет получение всех сеансов с деталями фильма и зала.
     * Ожидается:
     * - Возвращается коллекция FilmSessionDto
     * - Проверяются все поля DTO:
     *   - Название фильма и зала
     *   - Длительность сеанса
     *   - Цена билета
     *   - Время начала и окончания
     */
    @Test
    void whenFindAllWithFilmAndHallThenReturnDtoList() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        FilmSession session = new FilmSession(1, 1, 1,
                toTimestamp(start), toTimestamp(end), 500);
        Film film = new Film(1, "Film", "Description", 2023, 1, 16, 120, 1);
        Hall hall = new Hall(1, "Hall 1", 10, 15, "Description");

        when(filmSessionRepository.findAll()).thenReturn(List.of(session));
        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(hallRepository.findById(1)).thenReturn(Optional.of(hall));

        Collection<FilmSessionDto> result = filmSessionService.findAllWithFilmAndHall();

        assertThat(result).hasSize(1);
        FilmSessionDto dto = result.iterator().next();
        assertThat(dto.getFilmName()).isEqualTo("Film");
        assertThat(dto.getHallName()).isEqualTo("Hall 1");
        assertThat(dto.getDuration()).isEqualTo("2:0");
        assertThat(dto.getPrice()).isEqualTo(500);
        assertThat(dto.getStartTime()).isEqualTo(start);
        assertThat(dto.getEndTime()).isEqualTo(end);
    }

    /**
     * Тест проверяет поиск сеанса по ID с деталями фильма и зала.
     * Ожидается:
     * - Возвращается FilmSessionDto с полной информацией:
     *   - ID, название фильма и зала
     *   - Форматированная длительность
     *   - Цена билета
     *   - Время начала и окончания
     */
    @Test
    void whenFindByIdWithFilmAndHallThenReturnDto() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1).plusMinutes(30);
        FilmSession session = new FilmSession(1, 1, 1,
                toTimestamp(start), toTimestamp(end), 350);
        Film film = new Film(1, "Test Film", "Desc", 2023, 1, 12, 90, 1);
        Hall hall = new Hall(1, "Main Hall", 15, 20, "Big hall");

        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(hallRepository.findById(1)).thenReturn(Optional.of(hall));

        Optional<FilmSessionDto> result = filmSessionService.findByIdWithFilmAndHall(1);

        assertThat(result).isPresent();
        FilmSessionDto dto = result.get();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFilmName()).isEqualTo("Test Film");
        assertThat(dto.getHallName()).isEqualTo("Main Hall");
        assertThat(dto.getDuration()).isEqualTo("1:30");
        assertThat(dto.getPrice()).isEqualTo(350);
        assertThat(dto.getStartTime()).isEqualTo(start);
        assertThat(dto.getEndTime()).isEqualTo(end);
    }

    /**
     * Тест проверяет обработку ситуации, когда фильм не найден.
     * Ожидается:
     * - Бросается IllegalStateException
     * - Сообщение об ошибке содержит информацию о ненайденном фильме
     */
    @Test
    void whenFindByIdWithFilmAndHallFilmNotFoundThenThrowException() {
        LocalDateTime now = LocalDateTime.now();
        FilmSession session = new FilmSession(1, 999, 1,
                toTimestamp(now), toTimestamp(now.plusHours(2)), 500);
        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(filmRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmSessionService.findByIdWithFilmAndHall(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Film not found");
    }

    /**
     * Тест проверяет обработку ситуации, когда зал не найден.
     * Ожидается:
     * - Бросается IllegalStateException
     * - Сообщение об ошибке содержит информацию о ненайденном зале
     */
    @Test
    void whenFindByIdWithFilmAndHallHallNotFoundThenThrowException() {
        LocalDateTime now = LocalDateTime.now();
        FilmSession session = new FilmSession(1, 1, 999,
                toTimestamp(now), toTimestamp(now.plusHours(2)), 500);
        Film film = new Film(1, "Film", "Desc", 2023, 1, 12, 90, 1);
        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(hallRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmSessionService.findByIdWithFilmAndHall(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Hall not found");
    }
}