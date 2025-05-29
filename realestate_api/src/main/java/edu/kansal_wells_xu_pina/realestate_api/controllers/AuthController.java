package edu.kansal_wells_xu_pina.realestate_api.controllers;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;


@Controller
@RequestMapping("/landing-page")
public class UserController {

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
}
