package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.User;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserRepositorySql2oTest {

    private static UserRepositorySql2o userRepositorySql2o;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = UserRepositorySql2oTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        userRepositorySql2o = new UserRepositorySql2o(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("TRUNCATE TABLE users");
            query.executeUpdate();
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = new User(0, "John Doe", "john@example.com", "password");
        var savedUser = userRepositorySql2o.save(user).get();
        var foundUser = userRepositorySql2o.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(foundUser).usingRecursiveComparison().isEqualTo(savedUser);
    }

    @Test
    public void whenSaveDuplicateEmailThenEmptyOptional() {
        var user1 = new User(0, "John Doe", "john@example.com", "password");
        var user2 = new User(0, "Jane Doe", "john@example.com", "password2");
        userRepositorySql2o.save(user1);
        var result = userRepositorySql2o.save(user2);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindByEmailAndPasswordThenGetUser() {
        var user = new User(0, "John Doe", "john@example.com", "password");
        userRepositorySql2o.save(user);
        var foundUser = userRepositorySql2o.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(foundUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenFindByWrongEmailAndPasswordThenEmptyOptional() {
        var user = new User(0, "John Doe", "john@example.com", "password");
        userRepositorySql2o.save(user);
        var result = userRepositorySql2o.findByEmailAndPassword("wrong@example.com", "wrong");
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindByEmailThenGetUser() {
        var user = new User(0, "John Doe", "john@example.com", "password");
        userRepositorySql2o.save(user);
        var foundUser = userRepositorySql2o.findByEmail(user.getEmail()).get();
        assertThat(foundUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenFindByWrongEmailThenEmptyOptional() {
        var user = new User(0, "John Doe", "john@example.com", "password");
        userRepositorySql2o.save(user);
        var result = userRepositorySql2o.findByEmail("wrong@example.com");
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(userRepositorySql2o.findByEmail("nonexistent@example.com")).isEqualTo(Optional.empty());
        assertThat(userRepositorySql2o.findByEmailAndPassword("nonexistent@example.com", "password")).isEqualTo(Optional.empty());
    }
}