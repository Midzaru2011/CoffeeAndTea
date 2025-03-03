package ru.intf.sasha.controller;

import org.springframework.security.core.Authentication;
import ru.intf.sasha.model.Coffee;
import ru.intf.sasha.model.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

@Controller
@SessionAttributes("order")
public class CoffeeController {

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
        model.addAttribute("coffees", getAvailableCoffees());
        return "index";
    }

    @PostMapping("/add")
    public String addCoffee(@RequestParam String coffeeName, Model model) {
        Coffee coffee = getCoffeeByName(coffeeName);
        if (coffee != null) {
            Order order = (Order) model.getAttribute("order");
            order.addCoffee(coffee);
        }
        return "redirect:/";
    }

    @PostMapping("/checkout")
    public String checkout(Model model) {
        Order order = (Order) model.getAttribute("order");
        model.addAttribute("order", order);
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("Заказ оформлен ")
                .append(LocalDateTime.now())
                .append(":\n");
        for (Coffee coffee : order.getCoffees()) {
            orderDetails.append("- ")
                    .append(coffee.getName())
                    .append(" (")
                    .append(coffee.getPrice())
                    .append(" руб.)\n");
        }
        orderDetails.append("Общая стоимость: ")
                .append(order.getTotal())
                .append(" руб.");

        logger.info(orderDetails.toString());
        // Очищаем заказ после рендеринга страницы
        //clearOrder(model);

        return "checkout";
    }

    @PostMapping("/remove")
    public String removeCoffee(@RequestParam String coffeeName, Model model) {
        Order order = (Order) model.getAttribute("order");
        order.removeCoffee(coffeeName); // Удаляем напиток из заказа
        return "redirect:/";
    }

//    @GetMapping("/")
//    public String home(Model model, Authentication authentication) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            model.addAttribute("username", authentication.getName());
//        } else {
//            model.addAttribute("username", "Гость");
//        }
//        return "index";
//    }
    private void clearOrder(Model model) {
        Order order = (Order) model.getAttribute("order");
        order.clear(); // Добавьте метод clear() в класс Order для очистки заказа
        model.addAttribute("order", order);
    }

    private Coffee[] getAvailableCoffees() {
        return new Coffee[]{
                new Coffee("Эспрессо", 120, "/images/espresso.jpg"),
                new Coffee("Латте", 150, "/images/latte.jpg"),
                new Coffee("Капучино", 140, "/images/cappuccino.jpg"),
                new Coffee("Американо", 100, "/images/americano.jpg")
        };
    }
    private static final Logger logger = LoggerFactory.getLogger(CoffeeController.class);

    private Coffee getCoffeeByName(String name) {
        for (Coffee coffee : getAvailableCoffees()) {
            if (coffee.getName().equals(name)) {
                return coffee;
            }
        }
        return null;
    }
}