package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GenreRepositorySql2oTest {

    private static GenreRepositorySql2o genreRepositorySql2o;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = GenreRepositorySql2oTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        genreRepositorySql2o = new GenreRepositorySql2o(sql2o);

        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                    "INSERT INTO genres(name) VALUES ('Test Genre 1'), ('Test Genre 2')");
            query.executeUpdate();
        }
    }

    @AfterAll
    public static void clearGenres() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM genres WHERE id > 8");
            query.executeUpdate();
        }
    }

    @Test
    public void whenFindByIdThenGetGenre() {
        var genre = genreRepositorySql2o.findById(1).get();
        assertThat(genre.getName()).isEqualTo("Action");
    }

    @Test
    public void whenFindByInvalidIdThenEmpty() {
        var result = genreRepositorySql2o.findById(11);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindAllThenGetAllGenres() {
        Collection<Genre> genres = genreRepositorySql2o.findAll();
        assertThat(genres.size()).isEqualTo(10);
        assertThat(genres.stream().anyMatch(g -> g.getName().equals("Test Genre 1"))).isTrue();
        assertThat(genres.stream().anyMatch(g -> g.getName().equals("Test Genre 2"))).isTrue();
    }
}