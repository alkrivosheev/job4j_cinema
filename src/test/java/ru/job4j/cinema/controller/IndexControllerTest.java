package ru.job4j.cinema.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.HallService;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IndexController.class)
class IndexControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FilmService filmService;

    @MockitoBean
    private HallService hallService;

    @Autowired
    private ObjectMapper mapper;

    private Film film;

    private FilmDto filmDto;

    private Hall hall;

    @BeforeEach
    public void init() throws Exception {
        filmDto = FilmDto.builder().name("film2").build();
        hall = Hall.builder().name("hall1").build();
    }

    /**
     * Тест проверяет корректность отображения главной страницы.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Возвращение view "index"
     * - Наличие в модели атрибутов:
     *   - films: список DTO фильмов с информацией о жанрах
     *   - halls: список всех залов кинотеатра
     * - Данные в модели соответствуют ожидаемым значениям
     * - Сервисы возвращают корректные данные:
     *   - filmService.findAllWithGenre() возвращает список фильмов
     *   - hallService.findAll() возвращает список залов
     */
    @Test
    public void returnOKResponse() throws Exception {
        when(filmService.findAllWithGenre()).thenReturn(List.of(filmDto));
        when(hallService.findAll()).thenReturn(List.of(hall));

        mvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("films"))
                .andExpect(model().attributeExists("halls"))
                .andExpect(model().attribute("films", List.of(filmDto)))
                .andExpect(model().attribute("halls", List.of(hall)));
    }

}