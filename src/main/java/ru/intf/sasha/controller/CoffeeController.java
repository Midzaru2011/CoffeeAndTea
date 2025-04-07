package ru.intf.sasha.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Map;

import ru.intf.sasha.model.Coffee;
import ru.intf.sasha.model.Order;
import ru.intf.sasha.service.CoffeeService;

@Controller
@SessionAttributes("order")
public class CoffeeController {

    private final CoffeeService coffeeService;

    public CoffeeController(CoffeeService coffeeService) {
        this.coffeeService = coffeeService;
    }

    @ModelAttribute("order")
    public Order getOrder() {
        return new Order();
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) Boolean clearOrder,
            Model model,
            Authentication authentication) {
        if (Boolean.TRUE.equals(clearOrder)) {
            clearOrder(model); // Очищаем заказ, если указан параметр clearOrder=true
        }
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("username", "Гость");
        }
        model.addAttribute("coffees", coffeeService.getAllCoffees()); // Получаем напитки из БД
        return "index";
    }

    @PostMapping("/add")
    public String addCoffee(@RequestParam String coffeeName, Model model) {
        Coffee coffee = coffeeService.getCoffeeByName(coffeeName); // Находим напиток по имени
        if (coffee != null) {
            Order order = (Order) model.getAttribute("order");
            order.addCoffee(coffee);
        }
        return "redirect:/";
    }

    @PostMapping("/checkout")
    public String checkout(Model model) {
        // Получаем текущий заказ из модели
        Order order = (Order) model.getAttribute("order");

        // Формируем детали заказа
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("Заказ оформлен ")
                .append(LocalDateTime.now())
                .append(":\n");

        // Проходим по всем напиткам с их количеством
        assert order != null;
        for (Map.Entry<Coffee, Integer> entry : order.getCoffeesWithCounts().entrySet()) {
            Coffee coffee = entry.getKey();
            int quantity = entry.getValue();

            orderDetails.append("- ")
                    .append(coffee.getName())
                    .append(" (")
                    .append(coffee.getPrice())
                    .append(" руб.) x ")
                    .append(quantity) // Добавляем количество
                    .append(" = ")
                    .append(coffee.getPrice() * quantity) // Стоимость за все единицы
                    .append(" руб.\n");
        }

        orderDetails.append("Общая стоимость: ")
                .append(order.getTotal())
                .append(" руб.");

        // Логируем детали заказа
        logger.info(orderDetails.toString());

        // Передаем данные в модель для отображения на странице
        model.addAttribute("orderDetails", orderDetails.toString());

        // Очищаем заказ после оформления
//        clearOrder(model);

        return "checkout";
    }

    @PostMapping("/remove")
    public String removeCoffee(@RequestParam String coffeeName, Model model) {
        Order order = (Order) model.getAttribute("order");
        order.removeCoffee(coffeeName); // Удаляем напиток из заказа
        return "redirect:/";
    }

    private void clearOrder(Model model) {
        Order order = (Order) model.getAttribute("order");
        order.clear(); // Добавьте метод clear() в класс Order для очистки заказа
        model.addAttribute("order", order);
    }

    private static final Logger logger = LoggerFactory.getLogger(CoffeeController.class);
}