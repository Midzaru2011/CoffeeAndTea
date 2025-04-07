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

    public List<Coffee> getAllCoffees() {
        return coffeeRepository.findAll();
    }

    public Coffee getCoffeeByName(String name) {
        return coffeeRepository.findByName(name).orElse(null);
    }
}
