package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.job4j.cinema.repository.HallRepository;
import ru.job4j.cinema.service.FilmSessionService;

@Controller
public class ScheduleController {

    private final FilmSessionService filmSessionService;
    private final HallRepository hallRepository;

    public ScheduleController(FilmSessionService filmSessionService, HallRepository hallRepository) {
        this.filmSessionService = filmSessionService;
        this.hallRepository = hallRepository;
    }

    @GetMapping("/schedule")
    public String getAllSessions(Model model) {
        model.addAttribute("sessions", filmSessionService.findAllWithFilmAndHall());
        return "schedule";
    }

    @GetMapping("/schedule/{id}")
    public String getSessionById(@PathVariable int id, Model model) {
        var session = filmSessionService.findByIdWithFilmAndHall(id);
        if (session.isEmpty()) {
            return "redirect:/schedule";
        }
        model.addAttribute("film_session", session.get());
        model.addAttribute("hallRows", hallRepository.getRowCount(session.get().getHallId()));
        model.addAttribute("hallSeats", hallRepository.getPlaceCount(session.get().getHallId()));
        return "ticket-purchase";
    }
}