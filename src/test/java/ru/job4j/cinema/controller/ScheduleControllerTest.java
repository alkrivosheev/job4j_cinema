package ru.job4j.cinema.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.HallRepository;
import ru.job4j.cinema.service.FilmSessionService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ScheduleController.class)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FilmSessionService filmSessionService;

    @MockitoBean
    private HallRepository hallRepository;

    @Autowired
    private ObjectMapper mapper;

    private FilmSessionDto sessionDto;

    private User guestUser;
    private User authUser;

    @BeforeEach
    public void init() {
        sessionDto = new FilmSessionDto();
        sessionDto.setId(1);
        sessionDto.setFilmName("Test Film");
        sessionDto.setHallId(1);

        guestUser = new User();
        guestUser.setFullName("Гость");

        authUser = new User();
        authUser.setId(1);
        authUser.setFullName("Admin");
        authUser.setEmail("test@example.com");
        authUser.setPassword("password");
    }

    /**
     * Тест проверяет отображение расписания сеансов для неавторизованного пользователя.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Возвращение view "schedule"
     * - Наличие атрибута "sessions" в модели
     * - Список сеансов соответствует ожидаемому
     */
    @Test
    public void whenGetAllSessionsAsGuestThenReturnSchedulePage() throws Exception {
        when(filmSessionService.findAllWithFilmAndHall()).thenReturn(List.of(sessionDto));

        mvc.perform(get("/schedule")
                        .sessionAttr("user", guestUser))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule"))
                .andExpect(model().attributeExists("sessions"))
                .andExpect(model().attribute("sessions", List.of(sessionDto)));
    }

    /**
     * Тест проверяет отображение расписания сеансов для авторизованного пользователя.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Возвращение view "schedule"
     * - Наличие атрибута "sessions" в модели
     * - Список сеансов соответствует ожидаемому
     */
    @Test
    public void whenGetAllSessionsAsAuthUserThenReturnSchedulePage() throws Exception {
        when(filmSessionService.findAllWithFilmAndHall()).thenReturn(List.of(sessionDto));

        mvc.perform(get("/schedule")
                        .sessionAttr("user", authUser))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule"))
                .andExpect(model().attributeExists("sessions"))
                .andExpect(model().attribute("sessions", List.of(sessionDto)));
    }

    /**
     * Тест проверяет отображение страницы покупки билета для конкретного сеанса.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Возвращение view "ticket-purchase"
     * - Наличие всех необходимых атрибутов в модели:
     *   - film_session: информация о сеансе
     *   - hallRows: количество рядов в зале
     *   - hallSeats: количество мест в ряду
     * - Данные соответствуют ожидаемым значениям
     */
    @Test
    public void whenGetSessionByIdThenReturnTicketPurchasePage() throws Exception {
        int sessionId = 1;
        int rows = 5;
        int seats = 10;

        when(filmSessionService.findByIdWithFilmAndHall(sessionId))
                .thenReturn(Optional.of(sessionDto));
        when(hallRepository.getRowCount(sessionDto.getHallId())).thenReturn(rows);
        when(hallRepository.getPlaceCount(sessionDto.getHallId())).thenReturn(seats);

        mvc.perform(get("/schedule/{id}", sessionId)
                        .sessionAttr("user", authUser))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket-purchase"))
                .andExpect(model().attributeExists("film_session"))
                .andExpect(model().attributeExists("hallRows"))
                .andExpect(model().attributeExists("hallSeats"))
                .andExpect(model().attribute("film_session", sessionDto))
                .andExpect(model().attribute("hallRows", rows))
                .andExpect(model().attribute("hallSeats", seats));
    }

    /**
     * Тест проверяет обработку запроса несуществующего сеанса.
     * Ожидается:
     * - HTTP статус 302 (Redirect)
     * - Редирект на страницу расписания
     * - Возвращение view "redirect:/schedule"
     */
    @Test
    public void whenGetNonExistingSessionThenRedirectToSchedule() throws Exception {
        int nonExistingSessionId = 999;

        when(filmSessionService.findByIdWithFilmAndHall(nonExistingSessionId))
                .thenReturn(Optional.empty());

        mvc.perform(get("/schedule/{id}", nonExistingSessionId)
                        .sessionAttr("user", guestUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/schedule"));
    }
}