package ru.intf.sasha.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final List<Coffee> coffees = new ArrayList<>();

    public void addCoffee(Coffee coffee) {
        coffees.add(coffee);
    }

    public List<Coffee> getCoffees() {
        return coffees;
    }

    public double getTotal() {
        return coffees.stream().mapToDouble(Coffee::getPrice).sum();
    }
    public void clear() {
        coffees.clear();
    }

    // Метод для удаления напитка по имени
    public void removeCoffee(String coffeeName) {
        coffees.removeIf(coffee -> coffee.getName().equals(coffeeName));
    }
}

