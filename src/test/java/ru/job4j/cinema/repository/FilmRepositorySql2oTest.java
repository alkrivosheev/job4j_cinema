package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Film;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class FilmRepositorySql2oTest {

    private static FilmRepositorySql2o filmRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = FilmRepositorySql2oTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        filmRepository = new FilmRepositorySql2o(sql2o);
    }

    @Test
    public void whenFindByIdThenGetFilm() {
        var film = filmRepository.findById(1).get();
        assertThat(film.getName()).isEqualTo("Побег из Шоушенка");
        assertThat(film.getYear()).isEqualTo(1994);
        assertThat(film.getDurationInMinutes()).isEqualTo(142);
    }

    @Test
    public void whenFindByInvalidIdThenEmpty() {
        var result = filmRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    public void whenFindAllThenGetAllFilms() {
        Collection<Film> films = filmRepository.findAll();
        assertThat(films.size()).isEqualTo(8);
        var filmIds = films.stream().map(Film::getId).toList();
        assertThat(filmIds).contains(1, 3, 8);
    }

    @Test
    public void whenFindByAttributesThenCorrect() {
        var film = filmRepository.findById(2).get();
        assertThat(film.getGenreId()).isEqualTo(4);
        assertThat(film.getMinimalAge()).isEqualTo(18);
    }
}