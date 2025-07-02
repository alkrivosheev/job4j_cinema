package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    /**
     * Тест проверяет успешное сохранение билета на свободное место.
     * Ожидается:
     * - Возвращается сохраненный билет с присвоенным ID
     * - Проверяется вызов методов existsBySessionAndSeat и save репозитория
     */
    @Test
    void whenSaveAvailableSeatThenSuccess() {
        Ticket ticket = new Ticket(0, 1, 5, 10, 100);
        Ticket savedTicket = new Ticket(1, 1, 5, 10, 100);

        when(ticketRepository.existsBySessionAndSeat(1, 5, 10)).thenReturn(false);
        when(ticketRepository.save(ticket)).thenReturn(Optional.of(savedTicket));

        Optional<Ticket> result = ticketService.save(ticket);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(ticketRepository).existsBySessionAndSeat(1, 5, 10);
        verify(ticketRepository).save(ticket);
    }

    /**
     * Тест проверяет попытку сохранения билета на занятое место.
     * Ожидается:
     * - Возвращается пустой Optional
     * - Метод save репозитория не вызывается
     */
    @Test
    void whenSaveOccupiedSeatThenEmpty() {
        Ticket ticket = new Ticket(0, 1, 5, 10, 100);

        when(ticketRepository.existsBySessionAndSeat(1, 5, 10)).thenReturn(true);

        Optional<Ticket> result = ticketService.save(ticket);

        assertThat(result).isEmpty();
        verify(ticketRepository).existsBySessionAndSeat(1, 5, 10);
        verify(ticketRepository, never()).save(any());
    }

    /**
     * Тест проверяет обработку исключения при сохранении билета.
     * Ожидается:
     * - Возвращается пустой Optional при возникновении исключения
     */
    @Test
    void whenSaveThrowsExceptionThenEmpty() {
        Ticket ticket = new Ticket(0, 1, 5, 10, 100);

        when(ticketRepository.existsBySessionAndSeat(1, 5, 10)).thenReturn(false);
        when(ticketRepository.save(ticket)).thenThrow(new RuntimeException("DB error"));

        Optional<Ticket> result = ticketService.save(ticket);

        assertThat(result).isEmpty();
    }

    /**
     * Тест проверяет доступность места.
     * Ожидается:
     * - Возвращается true для свободного места
     */
    @Test
    void whenCheckAvailableSeatThenTrue() {
        when(ticketRepository.existsBySessionAndSeat(1, 5, 10)).thenReturn(false);

        boolean result = ticketService.isSeatAvailable(1, 5, 10);

        assertThat(result).isTrue();
    }

    /**
     * Тест проверяет занятость места.
     * Ожидается:
     * - Возвращается false для занятого места
     */
    @Test
    void whenCheckOccupiedSeatThenFalse() {
        when(ticketRepository.existsBySessionAndSeat(1, 5, 10)).thenReturn(true);

        boolean result = ticketService.isSeatAvailable(1, 5, 10);

        assertThat(result).isFalse();
    }

    /**
     * Тест проверяет поиск билетов по сеансу.
     * Ожидается:
     * - Возвращается коллекция всех билетов для указанного сеанса
     * - Количество и содержимое билетов соответствует ожидаемому
     */
    @Test
    void whenFindBySessionThenReturnTickets() {
        Ticket ticket1 = new Ticket(1, 1, 5, 10, 100);
        Ticket ticket2 = new Ticket(2, 1, 6, 11, 101);
        List<Ticket> expected = List.of(ticket1, ticket2);

        when(ticketRepository.findBySessionId(1)).thenReturn(expected);

        Collection<Ticket> result = ticketService.findBySession(1);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    /**
     * Тест проверяет поиск билета по сеансу и месту (существующий билет).
     * Ожидается:
     * - Возвращается Optional с найденным билетом
     * - Билет соответствует ожидаемому
     */
    @Test
    void whenFindBySessionAndSeatExistsThenReturnTicket() {
        Ticket expected = new Ticket(1, 1, 5, 10, 100);

        when(ticketRepository.findBySessionAndSeat(1, 5, 10))
                .thenReturn(Optional.of(expected));

        Optional<Ticket> result = ticketService.findBySessionAndSeat(1, 5, 10);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expected);
    }

    /**
     * Тест проверяет поиск билета по сеансу и месту (несуществующий билет).
     * Ожидается:
     * - Возвращается пустой Optional
     */
    @Test
    void whenFindBySessionAndSeatNotExistsThenEmpty() {
        when(ticketRepository.findBySessionAndSeat(1, 5, 10))
                .thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.findBySessionAndSeat(1, 5, 10);

        assertThat(result).isEmpty();
    }

    /**
     * Тест проверяет успешный поиск билета по существующему ID.
     * Ожидается:
     * - Возвращается Optional с найденным билетом
     * - Билет соответствует ожидаемому
     */
    @Test
    void whenFindByIdExistsThenReturnTicket() {
        Ticket expected = new Ticket(1, 1, 5, 10, 100);

        when(ticketRepository.findById(1))
                .thenReturn(Optional.of(expected));

        Optional<Ticket> result = ticketService.findById(1);

        assertThat(result)
                .isPresent()
                .contains(expected);
        verify(ticketRepository).findById(1);
    }

    /**
     * Тест проверяет поиск билета по несуществующему ID.
     * Ожидается:
     * - Возвращается пустой Optional
     */
    @Test
    void whenFindByIdNotExistsThenEmpty() {
        when(ticketRepository.findById(999))
                .thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.findById(999);

        assertThat(result).isEmpty();
        verify(ticketRepository).findById(999);
    }
}