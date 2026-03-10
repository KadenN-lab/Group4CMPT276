package com._6.group4.smartcart.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com._6.group4.smartcart.auth.User;
import com._6.group4.smartcart.auth.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final UserRepository userRepository;

    public AdminApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void checkAdmin(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("IS_ADMIN");

        if (isAdmin == null || !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers(HttpSession session) {
        checkAdmin(session);
        return userRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id, HttpSession session) {
        checkAdmin(session);
        userRepository.deleteById(id);
    }
}