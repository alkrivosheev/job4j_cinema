package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TicketRepositorySql2oTest {

    private static TicketRepositorySql2o ticketRepositorySql2o;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = TicketRepositorySql2oTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        ticketRepositorySql2o = new TicketRepositorySql2o(sql2o);
    }

    @AfterEach
    public void clearTickets() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("TRUNCATE TABLE tickets");
            query.executeUpdate();
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var ticket = new Ticket(1, 1, 5, 10, 2);
        var savedTicket = ticketRepositorySql2o.save(ticket).get();
        var foundTicket = ticketRepositorySql2o.findBySessionAndSeat(
                ticket.getSessionId(), ticket.getRowNumber(), ticket.getPlaceNumber()).get();
        assertThat(foundTicket).usingRecursiveComparison().isEqualTo(savedTicket);
    }

    @Test
    public void whenSaveDuplicateSeatThenEmptyOptional() {
        var ticket1 = new Ticket(0, 1, 5, 10, 100);
        var ticket2 = new Ticket(0, 1, 5, 10, 101);
        ticketRepositorySql2o.save(ticket1);
        var result = ticketRepositorySql2o.save(ticket2);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenExistsBySessionAndSeatThenTrue() {
        var ticket = new Ticket(0, 1, 5, 10, 100);
        ticketRepositorySql2o.save(ticket);
        var exists = ticketRepositorySql2o.existsBySessionAndSeat(
                ticket.getSessionId(), ticket.getRowNumber(), ticket.getPlaceNumber());
        assertThat(exists).isTrue();
    }

    @Test
    public void whenNotExistsBySessionAndSeatThenFalse() {
        var exists = ticketRepositorySql2o.existsBySessionAndSeat(1, 5, 10);
        assertThat(exists).isFalse();
    }

    @Test
    public void whenFindBySessionIdThenGetAllTickets() {
        var ticket1 = new Ticket(0, 1, 5, 10, 100);
        var ticket2 = new Ticket(0, 1, 6, 11, 101);
        var ticket3 = new Ticket(0, 2, 5, 10, 102);

        ticketRepositorySql2o.save(ticket1);
        ticketRepositorySql2o.save(ticket2);
        ticketRepositorySql2o.save(ticket3);

        var tickets = ticketRepositorySql2o.findBySessionId(1);
        assertThat(tickets.size()).isEqualTo(2);
        assertThat(tickets).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(ticket1, ticket2));
    }

    @Test
    public void whenFindBySessionAndSeatThenGetTicket() {
        var ticket = new Ticket(0, 1, 5, 10, 100);
        ticketRepositorySql2o.save(ticket);
        var foundTicket = ticketRepositorySql2o.findBySessionAndSeat(
                ticket.getSessionId(), ticket.getRowNumber(), ticket.getPlaceNumber()).get();
        assertThat(foundTicket).usingRecursiveComparison().isEqualTo(ticket);
    }

    @Test
    public void whenFindBySessionAndSeatNotFoundThenEmpty() {
        var result = ticketRepositorySql2o.findBySessionAndSeat(1, 5, 10);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindByNonExistingSessionIdThenEmptyCollection() {
        var tickets = ticketRepositorySql2o.findBySessionId(999);
        assertThat(tickets).isEqualTo(List.of());
    }

}