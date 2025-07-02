package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Ticket;
import java.util.Collection;
import java.util.Optional;

public interface TicketService {

    Optional<Ticket> save(Ticket ticket);

    boolean isSeatAvailable(int sessionId, int rowNumber, int placeNumber);

    Collection<Ticket> findBySession(int sessionId);

    Optional<Ticket> findBySessionAndSeat(int sessionId, int rowNumber, int placeNumber);

    Optional<Ticket> findById(int id);
}