package com.susanta.SwiftMart.services;

import com.susanta.SwiftMart.entities.CartItem;
import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;

import java.util.List;

public interface CartService {

    List<CartItem> getCartItems(User user);

    void addToCart(User user, Product product, int quantity);

    void updateCartItem(User user, Product product, int quantity);

    void removeCartItem(User user, Product product);

    void clearCart(User user);

    double getTotalPrice(User user);

    int getTotalItems(User user);

    List<CartItem> getCartItemsByCustomer(User user);
}
