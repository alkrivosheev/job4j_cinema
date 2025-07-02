package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Ticket;
import java.util.Collection;
import java.util.Optional;

public interface TicketRepository {

    Optional<Ticket> save(Ticket ticket);

    boolean existsBySessionAndSeat(int sessionId, int rowNumber, int placeNumber);

    Collection<Ticket> findBySessionId(int sessionId);

    Optional<Ticket> findBySessionAndSeat(int sessionId, int rowNumber, int placeNumber);

    Optional<Ticket> findById(int id);
}