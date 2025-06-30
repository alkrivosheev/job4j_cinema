package ru.job4j.cinema.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.service.FilmService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = KinotekaController.class)
class KinotekaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper mapper;

    private FilmDto filmDto;
    private Genre genre;

    @BeforeEach
    public void init() {
        filmDto = FilmDto.builder()
                .name("Test Film")
                .description("Test Description")
                .year(2023)
                .build();

        genre = new Genre(1, "Test Genre");
    }

    @Test
    public void whenGetKinotekaWithoutGenreThenReturnAllFilms() throws Exception {
        when(filmService.findAllWithGenre()).thenReturn(List.of(filmDto));
        when(filmService.findAllGenres()).thenReturn(List.of(genre));

        mvc.perform(get("/kinoteka"))
                .andExpect(status().isOk())
                .andExpect(view().name("kinoteka"))
                .andExpect(model().attributeExists("films"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attribute("films", List.of(filmDto)))
                .andExpect(model().attribute("genres", List.of(genre)));
    }

    @Test
    public void whenGetKinotekaWithGenreThenReturnFilteredFilms() throws Exception {
        int genreId = 1;
        when(filmService.findByGenreId(genreId)).thenReturn(List.of(filmDto));
        when(filmService.findAllGenres()).thenReturn(List.of(genre));

        mvc.perform(get("/kinoteka")
                        .param("genreId", String.valueOf(genreId)))
                .andExpect(status().isOk())
                .andExpect(view().name("kinoteka"))
                .andExpect(model().attributeExists("films"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attribute("films", List.of(filmDto)))
                .andExpect(model().attribute("genres", List.of(genre)));
    }
}