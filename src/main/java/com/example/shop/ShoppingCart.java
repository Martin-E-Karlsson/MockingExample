package com.example.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private final Map<Product, Integer> products = new HashMap<>();
    private final Map<Product, BigDecimal> productDiscounts = new HashMap<>();
    private BigDecimal totalDiscount = BigDecimal.valueOf(1);

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        products.merge(product, quantity, Integer::sum);
    }

    public void removeProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.remove(product);
        productDiscounts.remove(product);
    }

    public int getItemCount() {
        return products.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean containsProduct(Product product) {
        return products.containsKey(product);
    }

    public BigDecimal getTotalPrice() {
        BigDecimal subtotal = products.entrySet().stream()
                .map(entry -> entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalProductDiscounts = productDiscounts.entrySet().stream()
                .map(entry -> entry.getValue().multiply(BigDecimal.valueOf(products.getOrDefault(entry.getKey(), 1))))
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
        
        boolean productExists = products.containsKey(product);
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
        
        BigDecimal subtotal = products.entrySet().stream()
                .map(entry -> entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot be larger than subtotal");
        }
        
        totalDiscount = totalDiscount.subtract(discount);
    }

    public Map<Product, Integer> getProducts() {
        return new HashMap<>(products);
    }

    public void updateProductQuantity(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        if (quantity == 0) {
            products.remove(product);
            productDiscounts.remove(product);
        } else {
            products.put(product, quantity);
        }
    }

    public Map<Product, BigDecimal> getProductDiscounts() {
        return new HashMap<>(productDiscounts);
    }
}
