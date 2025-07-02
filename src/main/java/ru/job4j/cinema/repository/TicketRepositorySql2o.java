package ru.job4j.cinema.repository;

import org.sql2o.Sql2o;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Ticket;
import java.util.Collection;
import java.util.Optional;

@Repository
public class TicketRepositorySql2o implements TicketRepository {

    private final Sql2o sql2o;

    public TicketRepositorySql2o(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO tickets(session_id, row_number, place_number, user_id)
                    VALUES (:sessionId, :rowNumber, :placeNumber, :userId)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("sessionId", ticket.getSessionId())
                    .addParameter("rowNumber", ticket.getRowNumber())
                    .addParameter("placeNumber", ticket.getPlaceNumber())
                    .addParameter("userId", ticket.getUserId());

            int generatedId = query.executeUpdate().getKey(Integer.class);
            ticket.setId(generatedId);
            return Optional.of(ticket);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsBySessionAndSeat(int sessionId, int rowNumber, int placeNumber) {
        try (var connection = sql2o.open()) {
            var sql = """
                    SELECT EXISTS(
                        SELECT 1 FROM tickets 
                        WHERE session_id = :sessionId 
                        AND row_number = :rowNumber 
                        AND place_number = :placeNumber
                    )
                    """;
            return connection.createQuery(sql)
                    .addParameter("sessionId", sessionId)
                    .addParameter("rowNumber", rowNumber)
                    .addParameter("placeNumber", placeNumber)
                    .executeScalar(Boolean.class);
        }
    }

    @Override
    public Collection<Ticket> findBySessionId(int sessionId) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                            "SELECT * FROM tickets WHERE session_id = :sessionId")
                    .addParameter("sessionId", sessionId);
            return query.setColumnMappings(Ticket.COLUMN_MAPPING).executeAndFetch(Ticket.class);
        }
    }

    @Override
    public Optional<Ticket> findBySessionAndSeat(int sessionId, int rowNumber, int placeNumber) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                            "SELECT * FROM tickets WHERE session_id = :sessionId "
                                    + "AND row_number = :rowNumber AND place_number = :placeNumber")
                    .addParameter("sessionId", sessionId)
                    .addParameter("rowNumber", rowNumber)
                    .addParameter("placeNumber", placeNumber);
            return Optional.ofNullable(query.setColumnMappings(Ticket.COLUMN_MAPPING).executeAndFetchFirst(Ticket.class));
        }
    }

    @Override
    public Optional<Ticket> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM tickets WHERE id = :id")
                    .addParameter("id", id);
            Ticket ticket = query.setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetchFirst(Ticket.class);
            return Optional.ofNullable(ticket);
        }
    }
}