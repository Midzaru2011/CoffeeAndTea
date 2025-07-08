package ru.intf.sasha.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.intf.sasha.model.Coffee;
import ru.intf.sasha.service.CoffeeService;

@RestController
@RequestMapping("/api/coffees")
public class CoffeeApiController {

    private final CoffeeService coffeeService;

    public CoffeeApiController(CoffeeService coffeeService) {
        this.coffeeService = coffeeService;
    }

    @PostMapping
    public ResponseEntity<Coffee> addCoffee(@RequestBody Coffee coffee) {
        Coffee savedCoffee = coffeeService.addCoffee(coffee);
        return ResponseEntity.ok(savedCoffee);
    }

    @GetMapping("/info/{name}")
    public ResponseEntity<String> getCoffeeInfo(@PathVariable String name) {
        String description = coffeeService.getCoffeeDescription(name);
        return ResponseEntity.ok(description);
    }
}
