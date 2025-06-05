package edu.kansal_wells_xu_pina.realestate_api.controllers;
import edu.kansal_wells_xu_pina.realestate_api.enums.Role;
import jakarta.servlet.http.HttpServletResponse;
import edu.kansal_wells_xu_pina.realestate_api.services.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import org.springframework.ui.Model;
import edu.kansal_wells_xu_pina.realestate_api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/landing-page")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String landingPage() {
        return "landing-page";
    }

    @GetMapping("/login")
    public String displayLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login-form";
    }

    @GetMapping("/register")
    public String displayRegistrationForm() {
        return "registration-form";
    }

    // Finish implementation
    @PostMapping("/register")
    public String userRegistration(@ModelAttribute User user, HttpServletResponse response, Model model) {
        try {
            user.setRole(Role.BUYER);
            log.info("Role set to {}: ", user.getRole());
            authService.registerUser(user);
            Cookie jwtCookie = authService.loginAndCreateJwtCookie(user);
            response.addCookie(jwtCookie);
            log.info("User registered successfully: {}", user.getEmail());
            return "redirect:/landing-page/login";
        } catch (Exception e) {
            log.error("Registration failed for user: {}", user.getEmail(), e);
            model.addAttribute("error", e.getMessage());
            return "registration-form";
        }
    }

    // Finish implementation
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, HttpServletResponse response, Model model) {
        try {
            log.info("Attempting login for user: {}", user.getEmail());
            Cookie jwtCookie = authService.loginAndCreateJwtCookie(user);
            response.addCookie(jwtCookie);
            log.info("Login successful for user: {}", user.getEmail());
            return "redirect:/landing-page/dashboard";
        } catch (BadCredentialsException e) {
            log.error("Login failed for user: {}", user.getEmail(), e);
            model.addAttribute("error", "Invalid email or password");
            return "login-form";
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String displayDashboard(Model model) {
        try {
            userService.prepareDashboardModel(model);
            model.addAttribute("message", "Welcome to the dashboard");
            return "common-dashboard";
        } catch (Exception e) {
            log.error("Error preparing dashboard", e);
            model.addAttribute("error", "Error loading dashboard");
            return "error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        authService.clearJwtCookie(response);
        return "redirect:/landing-page/login";
    }
}
