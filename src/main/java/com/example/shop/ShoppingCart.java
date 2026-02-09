package com.example.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {
    private final List<Product> products = new ArrayList<>();
    private final Map<Product, BigDecimal> productDiscounts = new HashMap<>();
    private BigDecimal totalDiscount = BigDecimal.valueOf(1);

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
        productDiscounts.remove(product);
    }

    public int getItemCount() {
        return products.size();
    }

    public boolean containsProduct(Product product) {
        return products.contains(product);
    }

    public BigDecimal getTotalPrice() {
        BigDecimal subtotal = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalProductDiscounts = productDiscounts.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return subtotal.subtract(totalProductDiscounts).multiply(totalDiscount);
    }

    public void applyProductDiscount(Product product, BigDecimal discount) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (discount == null) {
            throw new IllegalArgumentException("Discount cannot be null");
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
        
        boolean productExists = products.contains(product);
        if (!productExists) {
            throw new IllegalArgumentException("Product must be in cart to apply discount");
        }
        
        BigDecimal productPrice = product.getPrice();
        if (discount.compareTo(productPrice) > 0) {
            throw new IllegalArgumentException("Discount cannot be larger than product price");
        }
        
        productDiscounts.put(product, discount);
    }

    public void applyTotalDiscount(BigDecimal discount) {
        if (discount == null) {
            throw new IllegalArgumentException("Discount cannot be null");
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
        
        BigDecimal subtotal = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot be larger than subtotal");
        }
        
        totalDiscount = totalDiscount.subtract(discount);
    }
}
