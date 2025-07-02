package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;
import java.util.Collection;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final FilmSessionService filmSessionService;
    private final FilmService filmService;

    public TicketController(TicketService ticketService,
                            FilmSessionService filmSessionService,
                            FilmService filmService) {
        this.ticketService = ticketService;
        this.filmSessionService = filmSessionService;
        this.filmService = filmService;
    }

    @PostMapping("/purchase")
    public String purchaseTicket(
            @RequestParam int sessionId,
            @RequestParam int rowNumber,
            @RequestParam int placeNumber,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        User user = (User) request.getAttribute("user");
        Ticket ticket = new Ticket();
        ticket.setSessionId(sessionId);
        ticket.setRowNumber(rowNumber);
        ticket.setPlaceNumber(placeNumber);
        ticket.setUserId(user.getId());

        Optional<Ticket> savedTicket = ticketService.save(ticket);
        if (savedTicket.isEmpty()) {
            redirectAttributes.addAttribute("rowNumber", rowNumber);
            redirectAttributes.addAttribute("placeNumber", placeNumber);
            return "redirect:/tickets/failure";
        }

        // Редирект с ID сохраненного билета
        redirectAttributes.addAttribute("ticketId", savedTicket.get().getId());
        return "redirect:/tickets/success";
    }

    @GetMapping("/success")
    public String showSuccessPage(
            @RequestParam int ticketId,
            Model model) {

        Ticket ticket = ticketService.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Билет не найден"));

        FilmSessionDto session = filmSessionService.findByIdWithFilmAndHall(ticket.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Сеанс не найден"));

        model.addAttribute("rowNumber", ticket.getRowNumber());
        model.addAttribute("placeNumber", ticket.getPlaceNumber());
        model.addAttribute("filmName", session.getFilmName());
        model.addAttribute("sessionTime", filmService.findById(session.getFilmId())
                .orElseThrow().getDurationInMinutes());
        model.addAttribute("price", session.getPrice());
        model.addAttribute("hallName", session.getHallName());

        return "tickets/success";
    }

    @GetMapping("/failure")
    public String showFailurePage(
            @RequestParam(required = false) Integer rowNumber,
            @RequestParam(required = false) Integer placeNumber,
            Model model) {
        if (rowNumber != null && placeNumber != null) {
            model.addAttribute("rowNumber", rowNumber);
            model.addAttribute("placeNumber", placeNumber);
        }
        return "tickets/failure";
    }

    @GetMapping("/session/{sessionId}")
    @ResponseBody
    public Collection<Ticket> getTicketsForSession(@PathVariable int sessionId) {
        return ticketService.findBySession(sessionId);
    }
}