package com.example.graphpilottest.javaxml;

public class Product {
    public final String name;
    public final String category;
    public final String price;
    public final String rating;
    public final String shipping;
    public final boolean popular;

    public Product(String name, String category, String price, String rating, String shipping, boolean popular) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.shipping = shipping;
        this.popular = popular;
    }
}
