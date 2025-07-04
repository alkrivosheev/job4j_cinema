package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;
import java.util.Collection;
import java.util.Optional;

@Repository
public class HallRepositorySql2o implements HallRepository {
    private final Sql2o sql2o;

    public HallRepositorySql2o(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Hall> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM halls WHERE id = :id")
                    .addParameter("id", id);
            var hall = query.setColumnMappings(Hall.COLUMN_MAPPING).executeAndFetchFirst(Hall.class);
            return Optional.ofNullable(hall);
        }
    }

    @Override
    public Collection<Hall> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM halls");
            return query.setColumnMappings(Hall.COLUMN_MAPPING).executeAndFetch(Hall.class);
        }
    }

    @Override
    public int getRowCount(int hallId) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT row_count FROM halls WHERE id = :hallId")
                    .addParameter("hallId", hallId);
            return query.executeScalar(Integer.class);
        }
    }

    @Override
    public int getPlaceCount(int hallId) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT place_count FROM halls WHERE id = :hallId")
                    .addParameter("hallId", hallId);
            return query.executeScalar(Integer.class);
        }
    }
}