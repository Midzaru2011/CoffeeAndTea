package ru.intf.sasha.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.intf.sasha.model.Coffee;
import ru.intf.sasha.repository.CoffeeRepository;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CoffeeRepository coffeeRepository;

    public DataInitializer(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, есть ли уже записи в таблице
        if (coffeeRepository.count() == 0) {
            // Добавляем начальные данные, только если таблица пуста
            List<Coffee> coffees = List.of(
                    new Coffee("Эспрессо", "Классический эспрессо", 120, "/images/espresso.jpg"),
                    new Coffee("Латте", "Молочный кофе с пенкой", 150, "/images/latte.jpg"),
                    new Coffee("Капучино", "Кофейный напиток с молочной пеной", 140, "/images/cappuccino.jpg"),
                    new Coffee("Американо", "Разбавленный эспрессо", 100, "/images/americano.jpg"),
                    new Coffee("Зеленый чай", "Освежающий зеленый чай", 80, "/images/green-tea.jpg") // Новая позиция
            );
            coffeeRepository.saveAll(coffees);
        }
    }
}
