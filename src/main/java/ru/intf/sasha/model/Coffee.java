package ru.intf.sasha.model;

public class Coffee {
    private String name;
    private double price;
    private String imageUrl;

    public Coffee(String name, double price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
    public String getImageUrl(){
        return imageUrl;
    }
}