package edu.kansal_wells_xu_pina.realestate_api.controllers;
import edu.kansal_wells_xu_pina.realestate_api.exceptions.InvalidUserParameterException;
import edu.kansal_wells_xu_pina.realestate_api.utils.JwtAuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import org.springframework.ui.Model;
import edu.kansal_wells_xu_pina.realestate_api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import org.springframework.security.access.prepost.PreAuthorize;



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
    public String loginUser(@ModelAttribute User user, HttpServletResponse httpResponse, Model model) {
        try {
            JwtAuthResponse jwtAuthResponse = authService.loginUser(user);
            Cookie cookie = new Cookie("jwt", jwtAuthResponse.getJwt());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            httpResponse.addCookie(cookie);
            return "redirect:/landing-page/dashboard";
        } catch (InvalidUserParameterException exception) {
            model.addAttribute("error", "Invalid username or password");
            return "login-form";
        }
    }

    @GetMapping("/dashboard")
    //@PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String displayDashboard(Model model) {
        model.addAttribute("message", "Welcome to the dashboard");
        return "common-dashboard";
    }
}
