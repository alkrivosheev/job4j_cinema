package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.File;
import java.util.Optional;

@Repository
public class FileRepositorySql2o implements FileRepository {
    private final Sql2o sql2o;

    public FileRepositorySql2o(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<File> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM files WHERE id = :id")
                    .addParameter("id", id);
            return Optional.ofNullable(query.executeAndFetchFirst(File.class));
        }
    }
}