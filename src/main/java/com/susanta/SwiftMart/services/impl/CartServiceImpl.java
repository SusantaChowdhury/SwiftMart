package com.susanta.SwiftMart.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.susanta.SwiftMart.entities.CartItem;
import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;
import com.susanta.SwiftMart.repositories.CartItemRepository;
import com.susanta.SwiftMart.services.CartService;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByCustomer(user);
    }

    @Override
    public void addToCart(User user, Product product, int quantity) {
        // Check if the product already exists in the user's cart
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(user, product).orElse(null);

        if (cartItem == null) {
            // Create a new cart item
            cartItem = new CartItem();
            cartItem.setCustomer(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setSubtotal(product.getPrice() * quantity);
        } else {
            // Update the quantity and subtotal
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setSubtotal(cartItem.getProduct().getPrice() * cartItem.getQuantity());
        }

        cartItemRepository.save(cartItem);
    }

    @Override
    public void updateCartItem(User user, Product product, int quantity) {
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(user, product).orElseThrow(
                () -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(quantity);
        cartItem.setSubtotal(cartItem.getProduct().getPrice() * quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void removeCartItem(User user, Product product) {
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(user, product).orElseThrow(
                () -> new RuntimeException("Cart item not found"));

        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(User user) {
        cartItemRepository.deleteByCustomer(user);
    }

    @Override
    public double getTotalPrice(User user) {
        return cartItemRepository.findByCustomer(user).stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    @Override
    public int getTotalItems(User user) {
        return cartItemRepository.findByCustomer(user).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    public List<CartItem> getCartItemsByCustomer(User user) {
        List<CartItem> cartItems = cartItemRepository.findByCustomer(user);
        Map<Product, CartItem> productMap = new HashMap<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (productMap.containsKey(product)) {
                CartItem existingCartItem = productMap.get(product);
                existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItem.getQuantity());
                existingCartItem.setSubtotal(existingCartItem.getSubtotal() + cartItem.getSubtotal());
            } else {
                productMap.put(product, cartItem);
            }
        }

        return new ArrayList<>(productMap.values());
    }
}
