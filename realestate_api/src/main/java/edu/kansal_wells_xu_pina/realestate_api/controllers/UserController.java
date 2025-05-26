package edu.kansal_wells_xu_pina.realestate_api.controllers;

@Controller
public class UserController {

    @GetMapping("/login")
    public String displayLoginForm() {
        return "login-form";
    }

    @GetMapping("/register")
    public String displayRegistrationForm() {
        return "registration-form";
    }
}
