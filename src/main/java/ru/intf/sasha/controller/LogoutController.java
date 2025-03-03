package ru.intf.sasha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("/perform_logout")
    public String showLogoutPage() {
        return "logout"; // Возвращает страницу logout.html
    }
}