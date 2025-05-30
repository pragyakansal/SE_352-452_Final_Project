package edu.kansal_wells_xu_pina.realestate_api.controllers;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import org.springframework.ui.Model;
import edu.kansal_wells_xu_pina.realestate_api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;


@Controller
@RequestMapping("/landing-page")
public class AuthController {
    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String landingPage() {
        return "landing-page";
    }

    @GetMapping("/login")
    public String displayLoginForm() {
        return "login-form";
    }

    @GetMapping("/register")
    public String displayRegistrationForm() {
        return "registration-form";
    }

    // Finish implementation
    @PostMapping("/register")
    public String userRegistration(@ModelAttribute User user, Model model) {
        authService.registerUser(user);
        return "redirect:/dashboard";
    }


    // Finish implementation
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        authService.loginUser(user);
        return "redirect:/dashboard";
    }
}
