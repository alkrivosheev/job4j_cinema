package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class FileRepositorySql2oTest {

    private static FileRepositorySql2o fileRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = FileRepositorySql2oTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        fileRepository = new FileRepositorySql2o(sql2o);
    }

    @Test
    public void whenFindByIdThenGetFile() {
        var file = fileRepository.findById(1).get();
        assertThat(file.getName()).isEqualTo("avatar.jpg");
        assertThat(file.getPath()).isEqualTo("files/avatar.jpg");
    }

    @Test
    public void whenFindByInvalidIdThenEmpty() {
        var result = fileRepository.findById(21);
        assertThat(result).isEmpty();
    }
}