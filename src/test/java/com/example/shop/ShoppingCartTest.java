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

    @Test
    @DisplayName("Should calculate total price for single product")
    void shouldCalculateTotalPriceForSingleProduct() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("1.50"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        
        // When
        BigDecimal total = cart.getTotalPrice();
        
        // Then
        assertThat(total).isEqualTo(new BigDecimal("1.50"));
    }

    @Test
    @DisplayName("Should calculate total price for multiple different products")
    void shouldCalculateTotalPriceForMultipleDifferentProducts() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("1.50"));
        Product banana = new Product("Banana", new BigDecimal("2.00"));
        Product orange = new Product("Orange", new BigDecimal("1.25"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        cart.addProduct(banana);
        cart.addProduct(orange);
        
        // When
        BigDecimal total = cart.getTotalPrice();
        
        // Then
        assertThat(total).isEqualTo(new BigDecimal("4.75"));
    }

    @Test
    @DisplayName("Should calculate total price for multiple identical products")
    void shouldCalculateTotalPriceForMultipleIdenticalProducts() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("1.50"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        cart.addProduct(apple);
        cart.addProduct(apple);
        
        // When
        BigDecimal total = cart.getTotalPrice();
        
        // Then
        assertThat(total).isEqualTo(new BigDecimal("4.50"));
    }

    @Test
    @DisplayName("Should return zero for empty cart total price")
    void shouldReturnZeroForEmptyCartTotalPrice() {
        // Given
        ShoppingCart cart = new ShoppingCart();
        
        // When
        BigDecimal total = cart.getTotalPrice();
        
        // Then
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @ParameterizedTest
    @DisplayName("Should handle various price calculations correctly")
    @CsvSource({
        "0.99, 0.99",
        "10.00, 10.00",
        "999.99, 999.99",
        "0.01, 0.01"
    })
    void shouldHandleVariousPriceCalculationsCorrectly(String priceStr, String expectedTotalStr) {
        // Given
        Product product = new Product("Test Product", new BigDecimal(priceStr));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(product);
        
        // When
        BigDecimal total = cart.getTotalPrice();
        
        // Then
        assertThat(total).isEqualTo(new BigDecimal(expectedTotalStr));
    }

    @Test
    @DisplayName("Should apply discount to individual product in cart")
    void shouldApplyDiscountToIndividualProductInCart() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("2.00"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        
        // When
        cart.applyProductDiscount(apple, new BigDecimal("0.50")); // $0.50 discount
        
        // Then
        assertThat(cart.getTotalPrice()).isEqualTo(new BigDecimal("1.50"));
    }

    @Test
    @DisplayName("Should apply percentage discount to individual product")
    void shouldApplyPercentageDiscountToIndividualProduct() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("4.00"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        
        // When
        cart.applyProductDiscount(apple, new BigDecimal("0.25")); // 25% discount
        
        // Then
        assertThat(cart.getTotalPrice()).isEqualTo(new BigDecimal("3.75"));
    }

    @Test
    @DisplayName("Should apply percentage discount to total purchase")
    void shouldApplyPercentageDiscountToTotalPurchase() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("2.00"));
        Product banana = new Product("Banana", new BigDecimal("3.75"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        cart.addProduct(banana);
        
        // When
        cart.applyTotalDiscount(new BigDecimal("0.10")); // 10% discount
        
        // Then
        assertThat(cart.getTotalPrice()).isEqualTo(new BigDecimal("5.1750"));
    }

    @Test
    @DisplayName("Should throw exception when applying discount to non-existent product")
    void shouldThrowExceptionWhenApplyingDiscountToNonExistentProduct() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("2.00"));
        Product orange = new Product("Orange", new BigDecimal("3.75"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cart.applyProductDiscount(orange, new BigDecimal("0.50")));
    }

    @Test
    @DisplayName("Should handle zero discount without issues")
    void shouldHandleZeroDiscountWithoutIssues() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("2.00"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        
        // When
        cart.applyProductDiscount(apple, BigDecimal.ZERO);
        
        // Then
        assertThat(cart.getTotalPrice()).isEqualTo(new BigDecimal("2.00"));
    }

    @Test
    @DisplayName("Should not allow discount larger than product price")
    void shouldNotAllowDiscountLargerThanProductPrice() {
        // Given
        Product apple = new Product("Apple", new BigDecimal("2.00"));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(apple);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cart.applyProductDiscount(apple, new BigDecimal("3.75")));
    }

    @ParameterizedTest
    @DisplayName("Should handle various discount calculations correctly")
    @CsvSource({
        "10.00, 2.00, 8.00",  // $2 off $10
        "5.50, 1.50, 4.00",  // $1.50 off $5.50
        "100.00, 25.00, 75.00" // $25 off $100
    })
    void shouldHandleVariousDiscountCalculationsCorrectly(String priceStr, String discountStr, String expectedTotalStr) {
        // Given
        Product product = new Product("Test Product", new BigDecimal(priceStr));
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(product);
        
        // When
        cart.applyProductDiscount(product, new BigDecimal(discountStr));
        
        // Then
        assertThat(cart.getTotalPrice()).isEqualTo(new BigDecimal(expectedTotalStr));
    }
}
