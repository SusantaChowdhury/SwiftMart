package com.susanta.SwiftMart.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.susanta.SwiftMart.entities.User;
import com.susanta.SwiftMart.helpers.ResourceNotFoundException;
import com.susanta.SwiftMart.repositories.UserRepo;
import com.susanta.SwiftMart.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    // Autowire the UserRepo here
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Optional<User> getUserById(String id) {
        // Implement the logic to get user by ID
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User u = userRepo.findById(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setName(user.getName());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        u.setPhone(user.getPhone());
        u.setAddress(user.getAddress());
        u.setRole(user.getRole());
        // u.setAbout(user.getAbout());
        u.setEnabled(false);
        u.setEmailVerificationToken(false);
        u.setProvider(user.getProvider());
        u.setProviderId(user.getProviderId());
        u.setProfilePic(user.getProfilePic());
        u.setEmailVerified(user.isEmailVerified());
        u.setPhoneVerified(user.isPhoneVerified());

        // save the user in db
        User saveUser = userRepo.save(u);

        return Optional.of(saveUser);
    }

    @Override
    public User saveUser(User user) {
        // generating user id
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        // encoding password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // set user role
        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
        logger.info(user.getProvider().toString());
        return userRepo.save(user);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepo.delete(user);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    public boolean isUserExistsByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        // check if user exists by email
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getSellerById(String id) {
        return userRepo.findById(id).orElse(null);
    }

}
