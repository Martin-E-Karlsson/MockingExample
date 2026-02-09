package com.example.shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ShoppingCartTest {

    @Test
    @DisplayName("Should add product to shopping cart and verify it contains one item")
    void shouldAddProductToCartAndVerifyContainsOneItem() {
        // Given
        Product product = new Product("Apple", new BigDecimal("1.50"));
        ShoppingCart cart = new ShoppingCart();
        
        // When
        cart.addProduct(product);
        
        // Then
        assertThat(cart.getItemCount()).isEqualTo(1);
        assertThat(cart.containsProduct(product)).isTrue();
    }

    @Test
    @DisplayName("Should add multiple identical products and increase item count")
    void shouldAddMultipleIdenticalProductsAndIncreaseItemCount() {
        // Given
        Product product = new Product("Apple", new BigDecimal("1.50"));
        ShoppingCart cart = new ShoppingCart();
        
        // When
        cart.addProduct(product);
        cart.addProduct(product);
        cart.addProduct(product);
        
        // Then
        assertThat(cart.getItemCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should throw exception when adding null product")
    void shouldThrowExceptionWhenAddingNullProduct() {
        // Given
        ShoppingCart cart = new ShoppingCart();
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cart.addProduct(null));
    }

    @Test
    @DisplayName("Should add different types of products to cart")
    void shouldAddDifferentTypesOfProductsToCart() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("1.50"));
        Product banana = new Product("Banana", new BigDecimal("2.00"));
        ShoppingCart cart = new ShoppingCart();
        
        // When
        cart.addProduct(apple);
        cart.addProduct(banana);
        
        // Then
        assertThat(cart.getItemCount()).isEqualTo(2);
        assertThat(cart.containsProduct(apple)).isTrue();
        assertThat(cart.containsProduct(banana)).isTrue();
    }

    @Test
    @DisplayName("Should remove product from shopping cart and verify item count decreases")
    void shouldRemoveProductFromCartAndVerifyItemCountDecreases() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("1.50"));
        Product banana = new Product("Banana", new BigDecimal("2.00"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        cart.addProduct(banana);
        
        // When
        cart.removeProduct(apple);
        
        // Then
        assertThat(cart.getItemCount()).isEqualTo(1);
        assertThat(cart.containsProduct(apple)).isFalse();
        assertThat(cart.containsProduct(banana)).isTrue();
    }

    @Test
    @DisplayName("Should remove all instances of identical product from cart")
    void shouldRemoveAllInstancesOfIdenticalProduct() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("1.50"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        cart.addProduct(apple);
        cart.addProduct(apple);
        
        // When
        cart.removeProduct(apple);
        
        // Then
        assertThat(cart.getItemCount()).isZero();
        assertThat(cart.containsProduct(apple)).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when trying to remove null product")
    void shouldThrowExceptionWhenRemovingNullProduct() {
        // Given
        ShoppingCart cart = new ShoppingCart();
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cart.removeProduct(null));
    }

    @Test
    @DisplayName("Should handle removing product that doesn't exist in cart")
    void shouldHandleRemovingProductThatDoesNotExistInCart() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("1.50"));
        Product banana = new Product("Banana", new BigDecimal("2.00"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        
        // When
        cart.removeProduct(banana);
        
        // Then
        assertThat(cart.getItemCount()).isEqualTo(1);
        assertThat(cart.containsProduct(apple)).isTrue();
        assertThat(cart.containsProduct(banana)).isFalse();
    }

    @Test
    @DisplayName("Should remove product from empty cart without issues")
    void shouldRemoveProductFromEmptyCart() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("1.50"));
        ShoppingCart cart = new ShoppingCart();
        
        // When
        cart.removeProduct(apple);
        
        // Then
        assertThat(cart.getItemCount()).isZero();
        assertThat(cart.containsProduct(apple)).isFalse();
    }
}
