package com._6.group4.smartcart.auth;

import com._6.group4.smartcart.models.User;
import com._6.group4.smartcart.auth.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final String SESSION_USER_ID = "USER_ID";
    private static final String SESSION_USER_EMAIL = "USER_EMAIL";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Object email = session.getAttribute(SESSION_USER_EMAIL);
        model.addAttribute("userEmail", email);
        return "design";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("userEmail", session.getAttribute(SESSION_USER_EMAIL));
        return "design";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model
    ) {
        try {
            authService.register(email, password, confirmPassword);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("email", email);
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "registered", required = false) String registered,
            @RequestParam(value = "logout", required = false) String loggedOut,
            Model model
    ) {
        if (registered != null) {
            model.addAttribute("message", "Registration successful. Please log in.");
        }
        if (loggedOut != null) {
            model.addAttribute("message", "You have been logged out.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        try {
            User user = authService.login(email, password);
            session.setAttribute(SESSION_USER_ID, user.getId());
            session.setAttribute(SESSION_USER_EMAIL, user.getEmail());
            return "redirect:/dashboard";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("email", email);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}

