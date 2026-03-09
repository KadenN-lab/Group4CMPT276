package com._6.group4.smartcart.infrastructure;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final String SESSION_USER_EMAIL = "USER_EMAIL";

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Object email = session.getAttribute(SESSION_USER_EMAIL);
        model.addAttribute("userEmail", email);
        return "design";
    }
}
