package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;
import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        try {
            if (!isSeatAvailable(ticket.getSessionId(),
                    ticket.getRowNumber(),
                    ticket.getPlaceNumber())) {
                return Optional.empty();
            }
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isSeatAvailable(int sessionId, int rowNumber, int placeNumber) {
        return !ticketRepository.existsBySessionAndSeat(sessionId, rowNumber, placeNumber);
    }

    @Override
    public Collection<Ticket> findBySession(int sessionId) {
        return ticketRepository.findBySessionId(sessionId);
    }

    @Override
    public Optional<Ticket> findBySessionAndSeat(int sessionId, int rowNumber, int placeNumber) {
        return ticketRepository.findBySessionAndSeat(sessionId, rowNumber, placeNumber);
    }
}