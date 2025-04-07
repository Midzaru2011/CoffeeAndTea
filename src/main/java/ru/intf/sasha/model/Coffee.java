package ru.intf.sasha.model;

import jakarta.persistence.*;

@Entity
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Уникальное ограничение
    private String name;
    private String description;
    private double price;
    private String imageUrl;

    // Конструктор без аргументов (для JPA)
    public Coffee() {}

    // Конструктор с аргументами
    public Coffee(String name, String description, double price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Проверяем, является ли объект самим собой
        if (o == null || getClass() != o.getClass()) return false; // Проверяем тип объекта

        Coffee coffee = (Coffee) o;

        // Сравниваем по ключевым полям (например, name, price)
        if (!name.equals(coffee.name)) return false;
        if (Double.compare(coffee.price, price) != 0) return false;
        return imageUrl != null ? imageUrl.equals(coffee.imageUrl) : coffee.imageUrl == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode(); // Используем name как часть хэша
        temp = Double.doubleToLongBits(price); // Используем price как часть хэша
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }
}