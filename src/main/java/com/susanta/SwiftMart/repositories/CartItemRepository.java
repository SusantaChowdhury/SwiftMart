package com.susanta.SwiftMart.repositories;

import com.susanta.SwiftMart.entities.CartItem;
import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find all cart items for a specific user
    List<CartItem> findByCustomer(User customer);

    // Find a specific cart item by user and product
    Optional<CartItem> findByCustomerAndProduct(User customer, Product product);

    // Delete all cart items for a specific user
    void deleteByCustomer(User customer);
}
