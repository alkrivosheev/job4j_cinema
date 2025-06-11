package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.HallService;

@ThreadSafe
@Controller
public class IndexController {
    private final FilmService filmService;
    private final HallService hallService;

    public IndexController(FilmService filmService, HallService hallService) {
        this.filmService = filmService;
        this.hallService = hallService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("films", filmService.findAllWithGenre());
        model.addAttribute("halls", hallService.findAll());
        return "index";
    }
}
