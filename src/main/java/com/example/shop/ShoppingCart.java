package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.add(product);
    }

    public void removeProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.removeIf(p -> p.equals(product));
    }

    public int getItemCount() {
        return products.size();
    }

    public boolean containsProduct(Product product) {
        return products.contains(product);
    }
}
