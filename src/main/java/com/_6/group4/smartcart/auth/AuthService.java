package com._6.group4.smartcart.auth;

import com._6.group4.smartcart.models.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User register(String email, String password, String confirmPassword, String name) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)) {
            throw new IllegalArgumentException("All fields are required.");
        }

        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Name is required.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }

        Optional<User> existing = userRepository.findByEmail(email.trim().toLowerCase());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        String passwordHash = passwordHasher.hash(password);
        User user = new User(normalizedEmail, passwordHash, name.trim());
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> existing = userRepository.findByEmail(normalizedEmail);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        User user = existing.get();
        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return user;
    }
}

