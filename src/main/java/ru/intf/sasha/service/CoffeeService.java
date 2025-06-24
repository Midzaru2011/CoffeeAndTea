package ru.intf.sasha.service;

import org.springframework.stereotype.Service;
import ru.intf.sasha.model.Coffee;
import ru.intf.sasha.repository.CoffeeRepository;
import ru.intf.sasha.ai.GigaChatAIAgent;
import java.util.List;

@Service
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;
    private final GigaChatAIAgent gigaChatAIAgent;

    public CoffeeService(CoffeeRepository coffeeRepository, GigaChatAIAgent gigaChatAIAgent) {
        this.coffeeRepository = coffeeRepository;
        this.gigaChatAIAgent = gigaChatAIAgent;
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
    public String getCoffeeDescription(String coffeeName) {
        return gigaChatAIAgent.getCoffeeInfo(coffeeName);
    }
}
