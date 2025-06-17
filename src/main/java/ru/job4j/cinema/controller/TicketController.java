package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final FilmSessionService filmSessionService;

    public TicketController(TicketService ticketService,
                            FilmSessionService filmSessionService) {
        this.ticketService = ticketService;
        this.filmSessionService = filmSessionService;
    }

    @PostMapping("/purchase")
    public String purchaseTicket(
            @RequestParam int sessionId,
            @RequestParam int rowNumber,
            @RequestParam int placeNumber,
            RedirectAttributes redirectAttributes) {
        if (!ticketService.isSeatAvailable(sessionId, rowNumber, placeNumber)) {
            redirectAttributes.addAttribute("rowNumber", rowNumber);
            redirectAttributes.addAttribute("placeNumber", placeNumber);
            return "redirect:/tickets/failure";
        }
        Ticket ticket = new Ticket();
        ticket.setSessionId(sessionId);
        ticket.setRowNumber(rowNumber);
        ticket.setPlaceNumber(placeNumber);
        Optional<Ticket> savedTicket = ticketService.save(ticket);
        if (savedTicket.isEmpty()) {
            redirectAttributes.addAttribute("rowNumber", rowNumber);
            redirectAttributes.addAttribute("placeNumber", placeNumber);
            return "redirect:/tickets/failure";
        }
        Optional<FilmSessionDto> sessionOpt = filmSessionService.findByIdWithFilmAndHall(sessionId);
        if (sessionOpt.isEmpty()) {
            return "redirect:/schedule";
        }
        FilmSessionDto session = sessionOpt.get();
        redirectAttributes.addAttribute("rowNumber", rowNumber);
        redirectAttributes.addAttribute("placeNumber", placeNumber);
        redirectAttributes.addAttribute("filmName", session.getFilmName());
        redirectAttributes.addAttribute("sessionTime",
                session.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        redirectAttributes.addAttribute("price", session.getPrice());
        redirectAttributes.addAttribute("hallName", session.getHallName());
        return "redirect:/tickets/success";
    }

    @GetMapping("/success")
    public String showSuccessPage(
            @RequestParam int rowNumber,
            @RequestParam int placeNumber,
            @RequestParam String filmName,
            @RequestParam String sessionTime,
            @RequestParam int price,
            @RequestParam String hallName,
            Model model) {
        model.addAttribute("rowNumber", rowNumber);
        model.addAttribute("placeNumber", placeNumber);
        model.addAttribute("filmName", filmName);
        model.addAttribute("sessionTime", sessionTime);
        model.addAttribute("price", price);
        model.addAttribute("hallName", hallName);
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