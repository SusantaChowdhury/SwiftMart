package com.susanta.SwiftMart.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.susanta.SwiftMart.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    // can write custome methodes and queries here
    // for example, findByEmail(String email) to find user by email
    Optional<User> findByEmail(String email);

    // Removed conflicting findById method as it is already provided by
    // CrudRepository
    // Optional<User> findByUsername(String username);
}
