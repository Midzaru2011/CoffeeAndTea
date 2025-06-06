package ru.intf.sasha.controller;

import ru.intf.sasha.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    // Отображение страницы авторизации (GET-запрос)
    @GetMapping("/login")
    public String login() {
        return "login"; // Возвращает HTML-страницу авторизации
    }

    // Обработка данных авторизации (POST-запрос)
    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestParam String username, @RequestParam String password) {
        // Проверка логина и пароля (замените на реальную логику, например, запрос к базе данных)
        if ("user".equals(username) && "password".equals(password)) {
            // Генерация JWT
            String token = JwtUtil.generateToken(username);

            // Возвращаем токен в ответе
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token) // Добавляем токен в заголовок
                    .body("Authentication successful. Token: " + token);
        } else {
            // Возвращаем ошибку при неверных данных
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        return "Scheme: " + request.getScheme() + ", Secure: " + request.isSecure();
    }
}