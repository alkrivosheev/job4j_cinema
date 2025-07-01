package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Hall;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HallRepositorySql2oTest {

    private static HallRepositorySql2o hallRepositorySql2o;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = HallRepositorySql2oTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        hallRepositorySql2o = new HallRepositorySql2o(sql2o);

        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                    "INSERT INTO halls(name, row_count, place_count, description) "
                            + "VALUES ('Test Hall', 10, 15, 'Test Description')");
            query.executeUpdate();
        }
    }

    @AfterEach
    public void clearHalls() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM halls WHERE id > 4");
            query.executeUpdate();
        }
    }

    /**
     * Тест проверяет поиск кинозала по существующему идентификатору.
     * Ожидается:
     * - Найденный кинозал имеет название "Gold Hall"
     * - Количество рядов: 6
     * - Количество мест в ряду: 12
     */
    @Test
    public void whenFindByIdThenGetHall() {
        var hall = hallRepositorySql2o.findById(4).get();
        assertThat(hall.getName()).isEqualTo("Gold Hall");
        assertThat(hall.getRowCount()).isEqualTo(6);
        assertThat(hall.getPlaceCount()).isEqualTo(12);
    }

    /**
     * Тест проверяет поиск по несуществующему идентификатору кинозала.
     * Ожидается:
     * - Результат должен быть пустым (Optional.empty())
     */
    @Test
    public void whenFindByInvalidIdThenEmpty() {
        var result = hallRepositorySql2o.findById(5);
        assertThat(result).isEqualTo(Optional.empty());
    }

    /**
     * Тест проверяет получение всех кинозалов из базы данных.
     * Ожидается:
     * - Общее количество кинозалов: 6 (4 стандартных + 2 тестовых)
     * - В списке присутствуют добавленные тестовые кинозалы:
     *   - "Test Hall"
     *   - "Second Hall"
     */
    @Test
    public void whenFindAllThenGetAllHalls() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                    "INSERT INTO halls(name, row_count, place_count, description) "
                            + "VALUES ('Second Hall', 5, 8, 'Small Hall')");
            query.executeUpdate();
        }

        Collection<Hall> halls = hallRepositorySql2o.findAll();
        assertThat(halls.size()).isEqualTo(6);
        assertThat(halls.stream().anyMatch(h -> h.getName().equals("Test Hall"))).isTrue();
        assertThat(halls.stream().anyMatch(h -> h.getName().equals("Second Hall"))).isTrue();
    }

}