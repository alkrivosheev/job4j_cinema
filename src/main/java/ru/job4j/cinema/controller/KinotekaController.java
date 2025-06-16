package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.cinema.service.FilmService;

@Controller
public class KinotekaController {

    private final FilmService filmService;

    public KinotekaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/kinoteka")
    public String kinoteka(
            @RequestParam(name = "genreId", required = false) Integer genreId,
            Model model) {

        if (genreId != null) {
            model.addAttribute("films", filmService.findByGenreId(genreId));
        } else {
            model.addAttribute("films", filmService.findAllWithGenre());
        }
        model.addAttribute("genres", filmService.findAllGenres());
        return "kinoteka";
    }
}
