package ru.intf.sasha.service;

import org.springframework.stereotype.Service;
import ru.intf.sasha.model.Coffee;
import ru.intf.sasha.repository.CoffeeRepository;

import java.util.List;

@Service
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;

    public CoffeeService(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }
    // Получить все напитки
    public List<Coffee> getAllCoffees() {
        return coffeeRepository.findAll();
    }
    // Найти напиток по имени
    public Coffee getCoffeeByName(String name) {
        return coffeeRepository.findByName(name).orElse(null);
    }
    // Добавить новый напиток
    public Coffee addCoffee(Coffee coffee) {
        return coffeeRepository.save(coffee);
    }
}
