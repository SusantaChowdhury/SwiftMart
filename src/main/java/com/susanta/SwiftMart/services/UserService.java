package com.susanta.SwiftMart.services;

import java.util.List;
import java.util.Optional;

import com.susanta.SwiftMart.entities.User;

public interface UserService {
    User saveUser(User user);

    Optional<User> getUserById(String id);

    Optional<User> updateUser(User user);

    void deleteUser(String id);

    boolean isEmailRegistered(String email);

    boolean isUserExistsByEmail(String email);

    List<User> getAllUsers();

    // User findByUsername(String username);

    User findByEmail(String email);

    public User getSellerById(String id);

    // add more service related methods if needed
}
