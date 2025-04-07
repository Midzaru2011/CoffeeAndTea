package ru.intf.sasha.model;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private final Map<Coffee, Integer> coffeeCounts = new HashMap<>();

    // Метод для добавления напитка
    public void addCoffee(Coffee coffee) {
        System.out.println("Adding coffee: " + coffee.getName());
        coffeeCounts.put(coffee, coffeeCounts.getOrDefault(coffee, 0) + 1);
    }

    // Метод для получения списка напитков с их количеством
    public Map<Coffee, Integer> getCoffeesWithCounts() {
        return coffeeCounts;
    }

    // Метод для расчета общей стоимости заказа
    public double getTotal() {
        return coffeeCounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    // Метод для очистки заказа
    public void clear() {
        coffeeCounts.clear();
    }

    // Метод для удаления напитка по имени
    public void removeCoffee(String coffeeName) {
        Coffee coffeeToRemove = null;

        // Находим напиток по имени
        for (Coffee coffee : coffeeCounts.keySet()) {
            if (coffee.getName().equals(coffeeName)) {
                coffeeToRemove = coffee;
                break;
            }
        }

        // Если напиток найден
        if (coffeeToRemove != null) {
            int currentQuantity = coffeeCounts.get(coffeeToRemove);

            if (currentQuantity > 1) {
                // Уменьшаем количество, если оно больше 1
                coffeeCounts.put(coffeeToRemove, currentQuantity - 1);
            } else {
                // Удаляем напиток, если количество равно 1
                coffeeCounts.remove(coffeeToRemove);
            }
        }
    }
}

