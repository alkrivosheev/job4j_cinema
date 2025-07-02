package ru.job4j.cinema.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TicketController.class)
class TicketControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private FilmSessionService filmSessionService;

    @MockitoBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper mapper;

    private User authUser;
    private FilmSessionDto sessionDto;
    private Ticket ticket;
    private Film film;

    @BeforeEach
    public void init() {
        authUser = new User();
        authUser.setId(1);
        authUser.setFullName("Test User");
        authUser.setEmail("test@example.com");
        authUser.setPassword("password");

        sessionDto = new FilmSessionDto();
        sessionDto.setId(1);
        sessionDto.setFilmId(1);
        sessionDto.setFilmName("Test Film");
        sessionDto.setHallId(1);
        sessionDto.setHallName("Test Hall");
        sessionDto.setPrice(500);

        film = new Film();
        film.setId(1);
        film.setName("Test Film");
        film.setDurationInMinutes(120);

        ticket = new Ticket();
        ticket.setId(1);
        ticket.setSessionId(1);
        ticket.setRowNumber(2);
        ticket.setPlaceNumber(3);
        ticket.setUserId(1);
    }

    /**
     * Тест проверяет успешную покупку билета.
     * Ожидается:
     * - Место доступно для бронирования
     * - Билет успешно сохраняется
     * - Сеанс и фильм существуют
     * - Редирект на страницу успешной покупки с параметрами
     */
    @Test
    public void whenPurchaseTicketSuccessfullyThenRedirectToSuccess() throws Exception {
        Ticket savedTicket = new Ticket();
        savedTicket.setId(1);
        savedTicket.setSessionId(1);
        savedTicket.setRowNumber(2);
        savedTicket.setPlaceNumber(3);
        savedTicket.setUserId(1);

        when(ticketService.isSeatAvailable(1, 2, 3)).thenReturn(true);
        when(ticketService.save(any(Ticket.class))).thenReturn(Optional.of(savedTicket));
        when(filmSessionService.findByIdWithFilmAndHall(1)).thenReturn(Optional.of(sessionDto));
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        mvc.perform(post("/tickets/purchase")
                        .param("sessionId", "1")
                        .param("rowNumber", "2")
                        .param("placeNumber", "3")
                        .requestAttr("user", authUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/tickets/success?*"));
    }

    /**
     * Тест проверяет обработку случая, когда место уже занято.
     * Ожидается:
     * - Место недоступно для бронирования
     * - Редирект на страницу ошибки с параметрами ряда и места
     */
    @Test
    public void whenSeatNotAvailableThenRedirectToFailure() throws Exception {
        when(ticketService.isSeatAvailable(1, 2, 3)).thenReturn(false);

        mvc.perform(post("/tickets/purchase")
                        .param("sessionId", "1")
                        .param("rowNumber", "2")
                        .param("placeNumber", "3")
                        .requestAttr("user", authUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets/failure?rowNumber=2&placeNumber=3"));
    }

    /**
     * Тест проверяет обработку ошибки при сохранении билета.
     * Ожидается:
     * - Место доступно
     * - Сохранение билета возвращает empty Optional
     * - Редирект на страницу ошибки с параметрами ряда и места
     */
    @Test
    public void whenSaveFailsThenRedirectToFailure() throws Exception {
        Ticket newTicket = new Ticket();
        newTicket.setSessionId(1);
        newTicket.setRowNumber(2);
        newTicket.setPlaceNumber(3);
        newTicket.setUserId(1);

        when(ticketService.isSeatAvailable(1, 2, 3)).thenReturn(true);
        when(ticketService.save(newTicket)).thenReturn(Optional.empty());

        mvc.perform(post("/tickets/purchase")
                        .param("sessionId", "1")
                        .param("rowNumber", "2")
                        .param("placeNumber", "3")
                        .requestAttr("user", authUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets/failure?rowNumber=2&placeNumber=3"));
    }

    /**
     * Тест проверяет отображение страницы успешной покупки.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Корректное имя view
     * - Наличие всех необходимых атрибутов в модели
     */
    @Test
    public void whenShowSuccessPageThenReturnSuccessView() throws Exception {
        when(ticketService.findById(1)).thenReturn(Optional.of(ticket));
        when(filmSessionService.findByIdWithFilmAndHall(1)).thenReturn(Optional.of(sessionDto));
        when(filmService.findById(1)).thenReturn(Optional.of(film));

        mvc.perform(get("/tickets/success")
                        .param("ticketId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/success"))
                .andExpect(model().attributeExists(
                        "rowNumber",
                        "placeNumber",
                        "filmName",
                        "sessionTime",
                        "price",
                        "hallName"))
                .andExpect(model().attribute("rowNumber", 2))
                .andExpect(model().attribute("placeNumber", 3))
                .andExpect(model().attribute("filmName", "Test Film"))
                .andExpect(model().attribute("sessionTime", 120))
                .andExpect(model().attribute("price", 500))
                .andExpect(model().attribute("hallName", "Test Hall"));
    }

    /**
     * Тест проверяет отображение страницы ошибки с параметрами.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Корректное имя view
     * - Наличие атрибутов ряда и места в модели
     */
    @Test
    public void whenShowFailurePageWithParamsThenReturnFailureViewWithParams() throws Exception {
        mvc.perform(get("/tickets/failure")
                        .param("rowNumber", "2")
                        .param("placeNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/failure"))
                .andExpect(model().attributeExists("rowNumber", "placeNumber"));
    }

    /**
     * Тест проверяет отображение страницы ошибки без параметров.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Корректное имя view
     * - Отсутствие ошибок при отсутствии параметров
     */
    @Test
    public void whenShowFailurePageWithoutParamsThenReturnFailureView() throws Exception {
        mvc.perform(get("/tickets/failure"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/failure"));
    }

    /**
     * Тест проверяет получение списка билетов для сеанса в формате JSON.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Корректный JSON с массивом билетов
     * - Соответствие возвращаемых данных ожидаемым
     */
    @Test
    public void whenGetTicketsForSessionThenReturnJson() throws Exception {
        when(ticketService.findBySession(1)).thenReturn(List.of(ticket));

        mvc.perform(get("/tickets/session/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(ticket))));
    }
}