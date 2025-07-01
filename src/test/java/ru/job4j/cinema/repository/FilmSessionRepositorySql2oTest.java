package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.FilmSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class FilmSessionRepositorySql2oTest {

    private static FilmSessionRepositorySql2o filmSessionRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = FilmSessionRepositorySql2oTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        filmSessionRepository = new FilmSessionRepositorySql2o(sql2o);
    }

    /**
     * Тест проверяет поиск сеанса по существующему идентификатору.
     * Ожидается:
     * - Найденный сеанс имеет корректные атрибуты:
     *   - Идентификатор фильма: 1
     *   - Цена билета: 350
     */
    @Test
    public void whenFindByIdThenGetFilmSession() {
        var session = filmSessionRepository.findById(1).get();
        assertThat(session.getFilmId()).isEqualTo(1);
        assertThat(session.getPrice()).isEqualTo(350);
    }

    /**
     * Тест проверяет поиск по несуществующему идентификатору сеанса.
     * Ожидается:
     * - Результат должен быть пустым (Optional.empty())
     */
    @Test
    public void whenFindByInvalidIdThenEmpty() {
        var result = filmSessionRepository.findById(11);
        assertThat(result).isEmpty();
    }

    /**
     * Тест проверяет получение всех сеансов из базы данных.
     * Примечание: порядок элементов не гарантируется.
     * Ожидается:
     * - Общее количество сеансов: 10
     * - В списке присутствуют сеансы с идентификаторами 1, 7 и 10
     */
    @Test
    public void whenFindAllThenGetAllSessionsWithoutOrderGuarantee() {
        Collection<FilmSession> sessions = filmSessionRepository.findAll();
        assertThat(sessions.size()).isEqualTo(10);
        var sessionIds = sessions.stream().map(FilmSession::getId).toList();
        assertThat(sessionIds).contains(1, 7, 10);
    }

    /**
     * Тест проверяет корректность получения сеансов по идентификатору фильма.
     * Ожидается:
     * - Найденный сеанс относится к фильму с идентификатором 1
     * - Цена билета: 400
     */
    @Test
    public void whenFindByFilmIdThenGetFilmSession() {
        var session = filmSessionRepository.findById(9).get();
        assertThat(session.getFilmId()).isEqualTo(1);
        assertThat(session.getPrice()).isEqualTo(400);
    }
}