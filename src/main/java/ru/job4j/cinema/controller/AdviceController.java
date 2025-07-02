package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception, Model model, HttpServletRequest request) {
        model.addAttribute("errorMessage", exception.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();
        model.addAttribute("stackTrace", stackTrace);

        model.addAttribute("url", request.getRequestURL());

        return "errors/500";
    }
}
