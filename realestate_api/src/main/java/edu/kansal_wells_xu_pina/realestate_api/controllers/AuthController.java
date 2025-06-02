package edu.kansal_wells_xu_pina.realestate_api.controllers;
import edu.kansal_wells_xu_pina.realestate_api.utils.JwtAuthResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import org.springframework.ui.Model;
import edu.kansal_wells_xu_pina.realestate_api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;


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
        return "redirect:/common-dashboard";
    }

    // Finish implementation
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> loginUser(@RequestBody User user) {
        JwtAuthResponse jwtAuthResponse = authService.loginUser(user);
        return ResponseEntity.ok(jwtAuthResponse);
        //return "redirect:/common-dashboard";
    }
}
